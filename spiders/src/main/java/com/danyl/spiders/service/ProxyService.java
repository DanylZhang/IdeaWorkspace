package com.danyl.spiders.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.jooq.DSLContext;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.danyl.spiders.constants.TimeConstants.MINUTES;
import static com.danyl.spiders.jooq.gen.proxy.tables.Proxy.PROXY;

@Slf4j
@Service
public class ProxyService {
    // 此处顺序不能变
    private static ProxyService instance = null;

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
        instance.proxy = this.proxy;
        instance.proxies = this.proxies;

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
                //.and(PROXY.COMMENT.startsWith("中国"))
                .fetch()
                .parallelStream()
                .map(proxyRecord -> {
                    String ip = proxyRecord.getIp();
                    Integer port = proxyRecord.getPort();
                    InetSocketAddress inetSocketAddress = new InetSocketAddress(ip, port);
                    return new Proxy(Proxy.Type.HTTP, inetSocketAddress);
                })
                .collect(Collectors.toList());

        // 这两句代码在高并发情况下会带来数据不安全，没有好办法，先在读线程内做自旋
        // fixed 解决方案，此处加写锁，用代理时加读锁
        instance.lock.writeLock().lock();
        instance.proxies.clear();
        instance.proxies.addAll(proxyList);
        instance.lock.writeLock().unlock();
        log.info("ProxyService update proxies success, count: {}", instance.proxies.size());
    }

    private Proxy get() {
        Proxy result = null;

        instance.lock.readLock().lock();
        int size = instance.proxies.size();
        int i = RandomUtils.nextInt(0, size);
        for (Proxy proxy0 : instance.proxies) {
            result = proxy0;
            if (--i == 0) {
                break;
            }
        }
        instance.lock.readLock().unlock();

        return result;
    }

    private void remove(Proxy proxy) {
        if (proxy == null) {
            return;
        }
        instance.lock.writeLock().lock();
        instance.proxies.remove(proxy);
        int size = instance.proxies.size();
        instance.lock.writeLock().unlock();

        if (size < 3) {
            instance.setProxies();
        }
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

    /**
     * 提供一个便捷的静态方法获取使用代理的 Jsoup Get
     *
     * @param url 目标网址
     */
    public static Document jsoupGet(String url) {
        return jsoupGet(url, ".");
    }

    /**
     * 提供一个便捷的静态方法获取使用代理的 Jsoup Get
     *
     * @param url   目标网址
     * @param regex response 校验正则，不符合预期的将被循环执行
     */
    public static Document jsoupGet(String url, String regex) {
        Response response = jsoupExecute(url, regex);
        Document document = null;
        try {
            document = response.parse();
        } catch (IOException e) {
            log.error("ProxyService.jsoupGet error: {}", e.getMessage());
        }
        return document;
    }

    /**
     * 提供一个便捷的静态方法获取使用代理的 Jsoup Get
     *
     * @param connection 指定了url以及一些参数的 jsoup Connection
     * @param regex      response 校验正则，不符合预期的将被循环执行
     */
    public static Document jsoupGet(Connection connection, String regex) {
        Response response = jsoupExecute(connection, regex);
        Document document = null;
        try {
            document = response.parse();
        } catch (IOException e) {
            log.error("ProxyService.jsoupGet error: {}", e.getMessage());
        }
        return document;
    }

    /**
     * 提供一个便捷的静态方法获取使用代理的 Jsoup Execute
     *
     * @param url 目标网址
     */
    public static Response jsoupExecute(String url) {
        Connection connect = Jsoup.connect(url);
        return jsoupExecute(connect, ".");
    }

    /**
     * 提供一个便捷的静态方法获取使用代理的 Jsoup Execute
     *
     * @param url   目标网址
     * @param regex response 校验正则，不符合预期的将被循环执行
     */
    public static Response jsoupExecute(String url, String regex) {
        Connection connect = Jsoup.connect(url);
        return jsoupExecute(connect, regex);
    }

    /**
     * 提供一个便捷的静态方法获取使用代理的 Jsoup Execute
     *
     * @param jsoupConnection 指定了url以及一些参数的 jsoup Connection
     * @param regex           response 校验正则，不符合预期的将被循环执行
     */
    public static Response jsoupExecute(Connection jsoupConnection, String regex) {
        final ProxyService instance = getInstance();
        while (true) {
            jsoupConnection.timeout(MINUTES / 2)
                    .followRedirects(true)
                    .ignoreContentType(true);
            // 1. 从proxies中拿到一个代理，并设置给jsoupConnection
            Proxy proxy0 = instance.get();
            if (proxy0 != null) {
                jsoupConnection.proxy(proxy0);
            } else {
                emptyProxyNeedSleep(jsoupConnection.request().url().toExternalForm());
            }
            try {
                Response execute = jsoupConnection.execute();
                if (Pattern.compile(regex).matcher(execute.body()).find()) {
                    return execute;
                }
            } catch (Exception e) {
                log.error("ProxyService.jsoupExecute error: {}", e.getMessage());
                if (proxy0 != null) {
                    instance.remove(proxy0);
                }
            }
        }
    }
}