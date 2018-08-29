package com.danyl.spiders.downloader;

import com.danyl.spiders.jooq.gen.proxy.tables.pojos.Proxy;
import com.danyl.spiders.service.ProxyService;
import com.danyl.spiders.utils.ProxyUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import static com.danyl.spiders.constants.TimeConstants.TIMEOUT;

@Slf4j
public class JsoupDownloader {
    private static ProxyService proxyService = ProxyService.getInstance();

    private static ExecutorService fixedThreadPool = new ThreadPoolExecutor(16
            , 32
            , 1
            , TimeUnit.MINUTES
            , new ArrayBlockingQueue<>(3000, true)
            , new ThreadFactoryBuilder().setDaemon(true).setNameFormat("JsoupDownloader-Worker-%d").build()
            , (r, executor) -> log.error("JsoupDownloader too busy so rejected tasks"));

    /**
     * 提供一个便捷的静态方法获取使用代理的 Jsoup Execute
     *
     * @param jsoupConnection 指定了url以及一些参数的 jsoup Connection
     * @param regex           response 校验正则，不符合预期的将被循环执行
     * @param useProxy        是否使用代理爬取
     * @return the jsoup execute response, maybe null when regex don't match
     */
    public static Connection.Response download(Connection jsoupConnection, String regex, Boolean useProxy, String anonymity) {
        Pattern pattern = Pattern.compile(regex);
        // 因为followRedirects会改变访问的URL，所以先保存URL
        String url = jsoupConnection.request().url().toExternalForm();
        // 返回未期望的结果时重试次数
        int retry = 5;

        while (true) {
            jsoupConnection.url(url)
                    .timeout(TIMEOUT)
                    .followRedirects(true)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");

            // 从proxies中拿到一个代理，并设置给jsoupConnection
            Proxy proxy0 = null;
            if (useProxy) {
                proxy0 = proxyService.get(url, anonymity);
            }
            if (proxy0 != null) {
                jsoupConnection.proxy(ProxyUtil.getProxy(proxy0));
            }
            try {
                Connection.Response execute = jsoupConnection.execute();
                String html = execute.body();
                if (pattern.matcher(html).find()) {
                    return execute;
                } else {
                    if (html.contains("<title>网站防火墙</title>")
                            || html.contains("safedog.cn")
                            || StringUtils.containsIgnoreCase("lightspeed", html)) {
                        continue;
                    }
                    // 此时链接访问正常，但是未返回期望的结果，
                    // 有可能目标链接包含的内容确实已经发生更改，
                    // 用户提供的regex未匹配结果是正常情况
                    // 故跳出死循环
                    if (retry-- <= 0) {
                        log.error("JsoupDownloader check regex error, url: {}, regex: {}, response: {}", url, regex, html.substring(0, 2000));
                    }
                }
            } catch (Exception e) {
                proxyService.remove(proxy0);
            }
        }
    }

    public static Connection.Response downloadNew(Connection jsoupConnection, String regex, Boolean useProxy, String anonymity) {
        Pattern pattern = Pattern.compile(regex);

        // 因为followRedirects会改变访问的URL，所以先保存URL
        Connection.Request request = jsoupConnection.request();
        final URL url = request.url();

        request.url(url)
                .timeout(TIMEOUT)
                .followRedirects(true)
                .ignoreContentType(true)
                .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");

        // 链接访问正常，但返回未匹配数据时的重试次数
        final AtomicInteger retry = new AtomicInteger(5);
        // 最终的响应结果
        final AtomicReference<Connection.Response> response = new AtomicReference<>(null);
        final CountDownLatch latch = new CountDownLatch(1);
        while (true) {
            for (int i = 0; i < 4; i++) {
                fixedThreadPool.submit(() -> {
                    Connection.Request newRequest = request;
                    // 从proxies中拿到一个代理，并设置给jsoupConnection
                    Proxy proxy0 = null;
                    if (useProxy) {
                        proxy0 = proxyService.get(url.toExternalForm(), anonymity);
                    }
                    if (proxy0 != null) {
                        newRequest.proxy(ProxyUtil.getProxy(proxy0));
                    }
                    try {
                        Connection.Response execute = Jsoup.connect(url.toExternalForm()).request(newRequest).execute();
                        if (pattern.matcher(execute.body()).find()) {
                            response.set(execute);
                            latch.countDown();
                        } else {
                            // 此时链接访问正常，但是未返回期望的结果，
                            // 有可能目标链接包含的内容确实已经发生更改，
                            // 用户提供的regex未匹配结果是正常情况
                            // 故跳出死循环
                            if (retry.decrementAndGet() <= 0) {
                                log.error("JsoupDownloader check regex error, url: {}, regex: {}, response: {}", url, regex, execute.body().substring(0, 1000));
                            }
                        }
                    } catch (Exception e) {
                        proxyService.remove(proxy0);
                    }
                });
            }
            try {
                latch.await(TIMEOUT, TimeUnit.MILLISECONDS);
                if (response.get() != null) {
                    return response.get();
                }
                if (retry.get() <= 0) {
                    return null;
                }
            } catch (Exception e) {
                log.error("JsoupDownloader error: {}, url: {}", e.getMessage(), url);
            }
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
        Connection.Response response = download(connection, regex, useProxy, null);
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
    public static Connection.Response jsoupExecute(String url, Boolean useProxy) {
        Connection connect = Jsoup.connect(url);
        return download(connect, ".", useProxy, null);
    }

    /**
     * 提供一个便捷的静态方法获取使用代理的 Jsoup Execute
     *
     * @param url   目标网址
     * @param regex response 校验正则，不符合预期的将被循环执行
     */
    public static Connection.Response jsoupExecute(String url, String regex) {
        Connection connect = Jsoup.connect(url);
        return download(connect, regex, true, null);
    }

    /**
     * 提供一个便捷的静态方法获取使用代理的 Jsoup Execute
     *
     * @param jsoupConnection 指定了url以及一些参数的 jsoup Connection
     * @param regex           response 校验正则，不符合预期的将被循环执行
     */
    public static Connection.Response jsoupExecute(Connection jsoupConnection, String regex) {
        return download(jsoupConnection, regex, true, null);
    }

    public static Connection.Response jsoupExecute(Connection jsoupConnection, String regex, Boolean useProxy) {
        return download(jsoupConnection, regex, useProxy, null);
    }
}