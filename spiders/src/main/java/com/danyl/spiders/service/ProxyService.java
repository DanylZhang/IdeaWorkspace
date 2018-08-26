package com.danyl.spiders.service;

import com.danyl.spiders.jooq.gen.proxy.tables.pojos.Proxy;
import com.danyl.spiders.utils.ProxyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import org.jooq.DSLContext;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.danyl.spiders.constants.ProtocolConstants.HTTP;
import static com.danyl.spiders.constants.ProtocolConstants.HTTPS;
import static com.danyl.spiders.constants.TimeConstants.TIMEOUT;
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
                .filter(proxy0 -> HTTPS.equals(proxy0.getProtocol()))
                .count();

        instance.lock.writeLock().lock();
        instance.proxies.clear();
        instance.proxies.addAll(proxyList);
        instance.lock.writeLock().unlock();
        log.info("ProxyService update proxies success, total proxy: {}, https: {}", totalCount, httpsCount);
    }

    /**
     * 根据即将访问的Url的协议类型，返回相对应协议的代理
     * 如果没有可用代理，将对此Url进行节流控制
     *
     * @param url 即将访问的Url
     */
    private Proxy get(String url) {
        Proxy result = null;
        String protocol = getUrlProtocol(url);

        instance.lock.readLock().lock();
        // fixed bug# subList是一个引用视图，即原数组的引用，并发写时会引发读抛出ConcurrentModificationException
        List<Proxy> tmpProxyList1 = new ArrayList<>(instance.proxies);
        instance.lock.readLock().unlock();

        List<Pair<Proxy, Double>> tmpProxyList2 = tmpProxyList1.stream()
                .filter(proxy0 -> {
                    if (HTTPS.equals(protocol)) {
                        return HTTPS.equals(proxy0.getProtocol()) || proxy0.getProtocol().toLowerCase().startsWith("socks");
                    } else {
                        return true;
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

    private void remove(Proxy proxy) {
        if (proxy == null) {
            return;
        }
        instance.lock.writeLock().lock();
        instance.proxies.remove(proxy);
        instance.lock.writeLock().unlock();
    }

    public static String getUrlProtocol(String url) {
        String protocol = HTTP;
        try {
            protocol = new URL(url).getProtocol();
        } catch (MalformedURLException e) {
            log.error("get url protocol error: {}", e.getMessage());
        }
        return protocol;
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
     * @param url      目标网址
     * @param useProxy 是否使用代理爬取
     */
    public static Document jsoupGet(String url, Boolean useProxy) {
        return jsoupGet(url, ".", useProxy);
    }

    /**
     * 提供一个便捷的静态方法获取使用代理的 Jsoup Get
     *
     * @param url   目标网址
     * @param regex response 校验正则，不符合预期的将被循环执行
     */
    public static Document jsoupGet(String url, String regex) {
        return jsoupGet(url, regex, true);
    }

    /**
     * 提供一个便捷的静态方法获取使用代理的 Jsoup Get
     *
     * @param url      目标网址
     * @param regex    response 校验正则，不符合预期的将被循环执行
     * @param useProxy 是否使用代理爬取
     */
    public static Document jsoupGet(String url, String regex, Boolean useProxy) {
        Connection connect = Jsoup.connect(url);
        return jsoupGet(connect, regex, useProxy);
    }

    /**
     * 提供一个便捷的静态方法获取使用代理的 Jsoup Get
     *
     * @param connection 指定了url以及一些参数的 jsoup Connection
     * @param regex      response 校验正则，不符合预期的将被循环执行
     */
    public static Document jsoupGet(Connection connection, String regex) {
        return jsoupGet(connection, regex, true);
    }

    /**
     * 提供一个便捷的静态方法获取使用代理的 Jsoup Get
     *
     * @param connection 指定了url以及一些参数的 jsoup Connection
     * @param regex      response 校验正则，不符合预期的将被循环执行
     * @param useProxy   是否使用代理爬取
     */
    public static Document jsoupGet(Connection connection, String regex, Boolean useProxy) {
        Response response = jsoupExecute(connection, regex, useProxy);
        if (response == null) {
            return null;
        }

        Document document = null;
        try {
            document = response.parse();
        } catch (IOException e) {
            log.error("jsoupGet error: {}", e.getMessage());
        }
        return document;
    }

    /**
     * 提供一个便捷的静态方法获取使用代理的 Jsoup Execute
     *
     * @param url      目标网址
     * @param useProxy 是否使用代理爬取
     */
    public static Response jsoupExecute(String url, Boolean useProxy) {
        Connection connect = Jsoup.connect(url);
        return jsoupExecute(connect, ".", useProxy);
    }

    /**
     * 提供一个便捷的静态方法获取使用代理的 Jsoup Execute
     *
     * @param url   目标网址
     * @param regex response 校验正则，不符合预期的将被循环执行
     */
    public static Response jsoupExecute(String url, String regex) {
        Connection connect = Jsoup.connect(url);
        return jsoupExecute(connect, regex, true);
    }

    /**
     * 提供一个便捷的静态方法获取使用代理的 Jsoup Execute
     *
     * @param url      目标网址
     * @param regex    response 校验正则，不符合预期的将被循环执行
     * @param useProxy 是否使用代理爬取
     */
    public static Response jsoupExecute(String url, String regex, Boolean useProxy) {
        Connection connect = Jsoup.connect(url);
        return jsoupExecute(connect, regex, useProxy);
    }

    public static Response jsoupExecute(Connection jsoupConnection, String regex) {
        return jsoupExecute(jsoupConnection, regex, true);
    }

    /**
     * 提供一个便捷的静态方法获取使用代理的 Jsoup Execute
     *
     * @param jsoupConnection 指定了url以及一些参数的 jsoup Connection
     * @param regex           response 校验正则，不符合预期的将被循环执行
     * @param useProxy        是否使用代理爬取
     * @return the jsoup execute response, maybe null when regex don't match
     */
    public static Response jsoupExecute(Connection jsoupConnection, String regex, Boolean useProxy) {
        // 获取代理的实例
        final ProxyService instance = getInstance();
        log.info("proxy service instance: {}", instance);

        // 因为followRedirects会改变访问的URL，所以先保存URL
        final String url = jsoupConnection.request().url().toExternalForm();
        // 链接访问正常，但返回未匹配数据时的重试次数
        int count = 20;
        Pattern pattern = Pattern.compile(regex);
        while (true) {
            jsoupConnection
                    .url(url)
                    .timeout(TIMEOUT)
                    .followRedirects(true)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");

            // 从proxies中拿到一个代理，并设置给jsoupConnection
            Proxy proxy0 = null;
            if (useProxy) {
                proxy0 = instance.get(url);
            }
            if (proxy0 != null) {
                jsoupConnection.proxy(ProxyUtil.getProxy(proxy0));
            }
            try {
                Response execute = jsoupConnection.execute();
                if (pattern.matcher(execute.body()).find()) {
                    return execute;
                } else {
                    // 此时链接访问正常，但是未返回期望的结果，
                    // 有可能目标链接包含的内容确实已经发生更改，
                    // 用户提供的regex未匹配结果是正常情况
                    // 故跳出死循环
                    if (count-- <= 0) {
                        log.error("jsoupExecute check regex error, url: {}, regex: {}, response: {}", url, regex, execute.body());
                        return null;
                    }
                }
            } catch (Exception e) {
                // log.error("jsoupExecute error: {}, url: {}, proxy: {}", e.getMessage(), url, proxy0);
                instance.remove(proxy0);
            }
        }
    }

    public static String chromeExecute(String url, By by, String regex, Boolean useProxy) {
        // 获取代理的实例
        final ProxyService instance = getInstance();
        log.info("proxy service instance: {}", instance);

        // 链接访问正常，但返回未匹配数据时的重试次数
        int count = 3;
        Pattern pattern = Pattern.compile(regex);
        while (true) {
            System.setProperty("webdriver.chrome.driver", "C:/Users/danyl/AppData/Local/360Chrome/Chrome/Application/chromedriver.exe");
            System.setProperty("phantomjs.binary.path", "D:/360极速浏览器下载/phantomjs-2.1.1-windows/bin/phantomjs.exe");
            ChromeOptions chromeOptions = new ChromeOptions();

            // 从proxies中拿到一个代理，并设置给chromeOptions
            Proxy proxy0 = null;
            if (useProxy) {
                proxy0 = instance.get(url);
            }
            if (proxy0 != null) {
                chromeOptions.setProxy(ProxyUtil.getSeleniumProxy(proxy0));
            }

            ChromeDriver chromeDriver = null;
            try {
                long start = System.currentTimeMillis();

                try {
                    chromeDriver = new ChromeDriver(chromeOptions);
                    // selenium bugs
                    // 当设置了pageLoadTimeout，并且当网络不好时
                    // 即使页面加载了99%，并不影响后续的findElement操作
                    // 也会抛出timeout异常
                    // chromeDriver.manage().timeouts().pageLoadTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
                    chromeDriver.manage().timeouts().implicitlyWait(TIMEOUT, TimeUnit.MILLISECONDS);
                } catch (Exception e) {
                    log.error("chrome execute new ChromeDriver error: {}", e.getMessage());
                    return null;
                }

                chromeDriver.get(url);
                String pageSource = chromeDriver.getPageSource();
                long end = System.currentTimeMillis();
                log.info("chrome execute get elapse: {}s", (end - start) / 1000);

                if (pattern.matcher(pageSource).find()) {
                    return pageSource;
                } else {
                    // 此时链接访问正常，但是未返回期望的结果，
                    // 有可能目标链接包含的内容确实已经发生更改，
                    // 用户提供的regex未匹配结果是正常情况
                    // 故跳出死循环
                    if (count-- <= 0) {
                        log.error("chromeExecute check selector error, url: {}, by: {}, response: {}", url, by, chromeDriver.getTitle());
                        return null;
                    }
                }
            } catch (Exception e) {
                // log.error("jsoupExecute error: {}, url: {}, proxy: {}", e.getMessage(), url, proxy0);
                instance.remove(proxy0);
            } finally {
                if (chromeDriver != null) {
                    chromeDriver.quit();
                }
            }
        }
    }
}