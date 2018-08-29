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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

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

    private List<Proxy> proxies = new ArrayList<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock(true); // 防止高并发下写锁拿不到

    // 无可用代理时使用本机直连访问，需要控制访问频次(防封ip)，以域名做精细化访问控制
    private static ConcurrentHashMap<String, AtomicInteger> accessControlCounterMap = new ConcurrentHashMap<>();

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
        List<Proxy> proxyList = instance.proxy.selectFrom(PROXY)
                .where(PROXY.IS_VALID.eq(true))
                .fetchInto(Proxy.class);

        int totalCount = proxyList.size();
        long httpsCount = proxyList.stream()
                .filter(proxy0 -> proxy0.getProtocol().contains(HTTPS))
                .count();
        long socksCount = proxyList.stream()
                .filter(proxy0 -> proxy0.getProtocol().contains(SOCKS))
                .count();

        instance.lock.writeLock().lock();
        instance.proxies.clear();
        instance.proxies.addAll(proxyList);
        instance.lock.writeLock().unlock();
        log.info("ProxyService.setProxies, total proxy: {}, https: {}, socks: {}, StackTrace: {}", totalCount, httpsCount, socksCount, Thread.currentThread().getStackTrace());
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

        instance.lock.readLock().lock();
        // fixed bug# subList是一个引用视图，即原数组的引用，并发写时会引发读抛出ConcurrentModificationException
        List<Proxy> tmpProxyList1 = new ArrayList<>(instance.proxies);
        instance.lock.readLock().unlock();

        List<Pair<Proxy, Double>> tmpProxyList2 = tmpProxyList1.stream()
                // 根据爬取的url类型来确定是否支持https
                .filter(proxy1 -> {
                    if (HTTPS.equals(protocol)) {
                        // 如果待爬取的url是https协议，则返回支持https的代理
                        return HTTPS.equals(proxy1.getProtocol()) || proxy1.getProtocol().toLowerCase().contains("socks");
                    } else {
                        // 如果待爬取的url是不是https协议，则返回所有可用代理
                        return true;
                    }
                })
                // 过滤符合指定匿名性的代理
                .filter(proxy1 -> {
                    if (StringUtils.isBlank(anonymity)) {
                        return true;
                    } else {
                        return StringUtils.containsIgnoreCase(anonymity, proxy1.getAnonymity());
                    }
                })
                // 代理的speed越快，probability就越大
                .map(proxy0 -> new Pair<>(proxy0, (double) (TIMEOUT - proxy0.getSpeed())))
                .collect(Collectors.toList());

        // 此时判断可用的https或http类型代理的数量
        if (tmpProxyList2.size() > 0) {
            try {
                EnumeratedDistribution<Proxy> proxyList = new EnumeratedDistribution<>(tmpProxyList2);
                result = proxyList.sample();
            } catch (Exception ignored) {
            }
        } else {
            instance.setProxies();
        }

        // 此处对无可用代理时进行节流处理
        if (result == null) {
            emptyProxyNeedSleep(url);
        }
        return result;
    }

    public void remove(Proxy proxy) {
        if (proxy == null) {
            return;
        }
        instance.lock.writeLock().lock();
        instance.proxies.remove(proxy);
        instance.lock.writeLock().unlock();
    }

    private static void emptyProxyNeedSleep(String url) {
        // 无可用代理时使用本机直连访问，需要控制访问频次(防封ip)，以域名做精细化访问控制
        final int frequency = 10;
        final ConcurrentHashMap<String, AtomicInteger> counter = ProxyService.accessControlCounterMap;
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