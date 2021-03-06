package com.danyl.spiders.service;

import com.danyl.spiders.jooq.gen.proxy.tables.pojos.Proxy;
import com.danyl.spiders.utils.ProxyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.danyl.spiders.constants.Constants.PROXY_TAKE_LIMIT;
import static com.danyl.spiders.constants.ProtocolConstants.HTTPS;
import static com.danyl.spiders.constants.ProtocolConstants.SOCKS;
import static com.danyl.spiders.constants.TimeConstants.TIMEOUT;
import static com.danyl.spiders.jooq.gen.proxy.tables.Proxy.PROXY;

@Slf4j
@Service
public class ProxyService {
    // 此处顺序不能变
    private static ProxyService instance;

    static {
        instance = new ProxyService();
    }

    @Resource(name = "DSLContextProxy")
    private DSLContext proxy;

    private Map<Proxy, Integer> proxies = new ConcurrentHashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    // 无可用代理时使用本机直连访问，需要控制访问频次(防封ip)，以域名做精细化访问控制
    private static ConcurrentHashMap<String, AtomicInteger> aclCounterMap = new ConcurrentHashMap<>();

    private ProxyService() {
    }

    @PostConstruct
    public void init() {
        instance = this;
        // 初始化时先填充好代理
        instance.setProxies();
    }

    //静态的工厂方法
    public static ProxyService getInstance() {
        return instance;
    }

    private void setProxies() {
        instance.proxy.selectFrom(PROXY)
                .where(PROXY.IS_VALID.eq(true))
                .fetchInto(Proxy.class)
                .forEach(proxy1 -> instance.proxies.putIfAbsent(proxy1, 0));

        Set<Proxy> proxies = instance.proxies.keySet();
        int totalCount = proxies.size();
        long httpsCount = proxies.stream()
                .filter(proxy1 -> proxy1.getProtocol().contains(HTTPS))
                .count();
        long socksCount = proxies.stream()
                .filter(proxy1 -> proxy1.getProtocol().contains(SOCKS))
                .count();
        long anonymousCount = proxies.stream()
                .filter(proxy1 -> Pattern.compile("(?i)L2|L3|L4|Anonymous|elite|高匿").matcher(proxy1.getAnonymity()).find())
                .count();
        log.info("ProxyService.setProxies, total proxy: {}, https: {}, socks: {}, anonymous: {}, StackTrace: {}", totalCount, httpsCount, socksCount, anonymousCount, Thread.currentThread().getStackTrace());
    }

    public Proxy get(String url) {
        return get(url, null);
    }

    /**
     * 根据即将访问的Url的协议类型，返回相对应协议的代理
     * 如果没有可用代理，将对此Url进行节流控制
     *
     * @param url 即将访问的Url
     */
    public Proxy get(String url, String anonymity) {
        Proxy result = null;
        String protocol = ProxyUtil.getUrlProtocol(url);

        while (result == null) {
            List<Pair<Proxy, Double>> tmpProxyList = instance.proxies.entrySet().stream()
                    // 没达到limit限制的代理可以继续参与提取
                    .filter(entry -> entry.getValue() < PROXY_TAKE_LIMIT)
                    .map(Map.Entry::getKey)
                    // 根据爬取的url类型来确定是否支持https
                    .filter(proxy1 -> matchProtocol(proxy1, protocol))
                    // 过滤符合指定匿名性的代理
                    .filter(proxy1 -> matchAnonymity(proxy1, anonymity))
                    // 代理的speed越快，probability就越大
                    .map(proxy1 -> new Pair<>(proxy1, (double) (TIMEOUT - proxy1.getSpeed())))
                    .collect(Collectors.toList());

            // 此时判断可用的https或http类型代理的数量
            if (tmpProxyList.size() > 0) {
                try {
                    EnumeratedDistribution<Proxy> proxyList = new EnumeratedDistribution<>(tmpProxyList);
                    result = proxyList.sample();
                    instance.takeIncr(result);
                } catch (Exception ignored) {
                }
            } else {
                long count = instance.proxies.entrySet().stream()
                        .map(Map.Entry::getKey)
                        // 根据爬取的url类型来确定是否支持https
                        .filter(proxy1 -> matchProtocol(proxy1, protocol))
                        // 过滤符合指定匿名性的代理
                        .filter(proxy1 -> matchAnonymity(proxy1, anonymity))
                        .count();
                if (count > 1) {
                    emptyProxyNeedSleep(url);
                } else {
                    if (lock.tryLock()) {
                        instance.setProxies();
                        lock.unlock();
                    }
                }
            }
        }
        return result;
    }

    public void takeIncr(Proxy proxy) {
        if (proxy == null) {
            return;
        }
        synchronized (proxy) {
            Integer count = instance.proxies.get(proxy);
            if (count != null) {
                instance.proxies.put(proxy, ++count);
            }
        }
    }

    public void takeDecr(Proxy proxy) {
        if (proxy == null) {
            return;
        }
        synchronized (proxy) {
            Integer count = instance.proxies.get(proxy);
            if (count != null) {
                instance.proxies.put(proxy, --count);
            }
        }
    }

    public void remove(Proxy proxy) {
        if (proxy == null) {
            return;
        }
        instance.proxies.remove(proxy);
    }

    private static Boolean matchProtocol(Proxy proxy, String protocol) {
        if (HTTPS.equals(protocol)) {
            // 如果待爬取的url是https协议，则返回支持https的代理
            return HTTPS.equals(proxy.getProtocol()) || proxy.getProtocol().toLowerCase().contains("socks");
        } else {
            // 如果待爬取的url是不是https协议，则返回所有可用代理
            return true;
        }
    }

    private static Boolean matchAnonymity(Proxy proxy, String anonymity) {
        if (StringUtils.isBlank(anonymity)) {
            return true;
        } else if (StringUtils.containsIgnoreCase(anonymity, "L1")) {
            return true;
        } else if (StringUtils.containsIgnoreCase(anonymity, "L2")) {
            return StringUtils.containsIgnoreCase(proxy.getAnonymity(), anonymity)
                    || StringUtils.containsIgnoreCase(proxy.getAnonymity(), "L3")
                    || StringUtils.containsIgnoreCase(proxy.getAnonymity(), "L4")
                    || StringUtils.containsIgnoreCase(proxy.getAnonymity(), "Anonymous")
                    || StringUtils.containsIgnoreCase(proxy.getAnonymity(), "elite")
                    || StringUtils.containsIgnoreCase(proxy.getAnonymity(), "高匿");
        } else if (StringUtils.containsIgnoreCase(anonymity, "L3")) {
            return StringUtils.containsIgnoreCase(proxy.getAnonymity(), anonymity)
                    || StringUtils.containsIgnoreCase(proxy.getAnonymity(), "L4");
        } else if (StringUtils.containsIgnoreCase(anonymity, "L4")) {
            return StringUtils.containsIgnoreCase(proxy.getAnonymity(), anonymity);
        } else {
            return StringUtils.containsIgnoreCase(proxy.getAnonymity(), anonymity);
        }
    }

    private static void emptyProxyNeedSleep(String url) {
        // 无可用代理时使用本机直连访问，需要控制访问频次(防封ip)，以域名做精细化访问控制
        final int frequency = 10;
        final ConcurrentHashMap<String, AtomicInteger> counter = ProxyService.aclCounterMap;
        try {
            String host = new URL(url).getHost();
            AtomicInteger count = counter.get(host);
            if (count == null) {
                counter.put(host, new AtomicInteger(0));
                count = counter.get(host);
            }

            // 简单使用sync，达到访问频次控制目的
            synchronized (count) {
                if (count.incrementAndGet() % frequency == 0) {
                    try {
                        int sleep = RandomUtils.nextInt(10, 20);
                        log.warn("ProxyService haven't proxy, so sleep {} seconds, url: {}", sleep, url);
                        Thread.sleep(sleep * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}