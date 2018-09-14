package com.danyl.spiders.downloader;

import com.danyl.spiders.jooq.gen.proxy.tables.pojos.Proxy;
import com.danyl.spiders.service.ProxyService;
import com.danyl.spiders.utils.ProxyUtil;
import com.google.common.collect.ImmutableSet;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.ProxyOptions;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import static com.danyl.spiders.constants.Constants.URL_RETRY;
import static com.danyl.spiders.constants.Constants.USERAGENT;
import static com.danyl.spiders.constants.TimeConstants.TIMEOUT;

@Slf4j
public class JsoupDownloader {
    private static ProxyService proxyService = ProxyService.getInstance();

    private static Vertx vertx = initVertx();

    private static Vertx initVertx() {
        VertxOptions vertxOptions = new VertxOptions();
        vertxOptions.setWorkerPoolSize(8);
        return Vertx.vertx(vertxOptions);
    }

    private static Boolean hasBlocked(String body) {
        ImmutableSet<String> blockFeatures = ImmutableSet.<String>builder()
                .add("<title>网站防火墙</title>")
                .add("<title>System Error</title>")
                .add("safedog.cn")
                .add("url.fortinet.net")
                .add("Squid Error pages")
                .add("Mikrotik HttpProxy")
                .add("logoSophosFooter")
                .add("The proxy server received")
                .add("Panel Komunikacyjny")
                .add("404 - Recurso no encontrado")
                .add("the server does not have a DNS entry")
                .add("a padding to disable MSIE and Chrome friendly error page")
                .add("<div id=x><div id=g>广告</div>")
                .add("Blocked because of DoS Attack")
                .add("<title>Сервис Интернет!</title>")
                .add("aviso.noroestenet.com.br")
                .add("<title>Box configuration</title>")
                .add("错误：您所请求的网址（URL）无法获取")
                .build();
        String str = String.join("|", blockFeatures);
        return Pattern.compile("(?i)" + str).matcher(body).find();
    }

    /**
     * Initialise Trust manager that does not validate certificate chains and
     * add it to current SSLContext.
     * <p/>
     * please not that this method will only perform action if sslSocketFactory is not yet
     * instantiated.
     *
     * @throws IOException on SSL init errors
     */
//    private static synchronized void initUnSecureTSL() throws IOException {
//        if (sslSocketFactory == null) {
//            // Create a trust manager that does not validate certificate chains
//            final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
//
//                public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
//                }
//
//                public void checkServerTrusted(final X509Certificate[] chain, final String authType) {
//                }
//
//                public X509Certificate[] getAcceptedIssuers() {
//                    return null;
//                }
//            }};
//
//            // Install the all-trusting trust manager
//            final SSLContext sslContext;
//            try {
//                sslContext = SSLContext.getInstance("SSL");
//                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
//                // Create an ssl socket factory with our all-trusting manager
//                sslSocketFactory = sslContext.getSocketFactory();
//            } catch (NoSuchAlgorithmException | KeyManagementException e) {
//                throw new IOException("Can't create unsecure trust manager");
//            }
//        }
//    }

    /**
     * 提供一个便捷的静态方法获取使用代理的 Jsoup Execute
     *
     * @param jsoupConnection 指定了url以及一些参数的 jsoup Connection
     * @param regex           response 校验正则，不符合预期的将被循环执行
     * @param options         DownloaderOptions
     * @return the jsoup execute response, maybe null when regex don't match
     */
    public static Connection.Response download(Connection jsoupConnection, String regex, DownloaderOptions options) {
        if (options == null) {
            options = new DownloaderOptions(true, "L2");
        }

        Pattern pattern = Pattern.compile(regex);
        // 因为followRedirects会改变访问的URL，所以先保存URL
        String url = jsoupConnection.request().url().toExternalForm();
        // 返回未期望的结果时重试次数
        int retry = URL_RETRY;

        while (true) {
            jsoupConnection.url(url)
                    .timeout(TIMEOUT)
                    .followRedirects(true)
                    .ignoreContentType(true)
                    .userAgent(USERAGENT);

            // 从proxies中拿到一个代理，并设置给jsoupConnection
            Proxy proxy1 = null;
            if (options.getUseProxy()) {
                proxy1 = proxyService.get(url, options.getAnonymity());
            }
            if (proxy1 != null) {
                jsoupConnection.proxy(ProxyUtil.getProxy(proxy1));
            }
            try {
                Connection.Response execute = jsoupConnection.execute();
                String body = execute.body();
                if (pattern.matcher(body).find()) {
                    proxyService.takeDecr(proxy1);
                    return execute;
                } else {
                    if (hasBlocked(body)) {
                        // log.info("JsoupDownloader.download has been blocked! proxy: {}, url: {}, regex: {}, response: {}", proxy1, url, regex, StringUtils.substring(body, 0, 1000));
                        proxyService.remove(proxy1);
                    } else {
                        // 此时链接访问正常，但是未返回期望的结果，
                        // 有可能目标链接包含的内容确实已经发生更改，
                        // 用户提供的regex未匹配结果是正常情况
                        // 故跳出死循环
                        if (retry-- <= 0) {
                            log.error("JsoupDownloader check regex error, url: {}, regex: {}, response: {}", url, regex, body);
                        }
                    }
                }
            } catch (Exception e) {
                proxyService.remove(proxy1);
            }
        }
    }

    private static WebClient newWebClient(Proxy proxy) {
        WebClientOptions webClientOptions = new WebClientOptions();
        // 开启对代理返回gzip解压缩
        webClientOptions.setTryUseCompression(true);
        // 跟随重定向
        webClientOptions.setFollowRedirects(true);
        // 爬完就关闭，不KeepAlive
        webClientOptions.setKeepAlive(false);
        webClientOptions.setUserAgentEnabled(true);
        webClientOptions.setUserAgent(USERAGENT);
        webClientOptions.setConnectTimeout(TIMEOUT / 3);

        if (proxy != null) {
            ProxyOptions proxyOptions = ProxyUtil.getProxyOptions(proxy);
            webClientOptions.setProxyOptions(proxyOptions);
        }
        return WebClient.create(vertx, webClientOptions);
    }

    public static String vertxDownload(Connection jsoupConnection, String regex, DownloaderOptions options) {
        if (options == null) {
            options = new DownloaderOptions(true, "L2");
        }

        Pattern pattern = Pattern.compile(regex);
        // 因为followRedirects会改变访问的URL，所以先保存URL
        Connection.Request request = jsoupConnection.request();
        final URL url = request.url();

        // 链接访问正常，但返回未匹配数据时的重试次数
        final AtomicInteger retry = new AtomicInteger(URL_RETRY);
        final AtomicReference<String> html = new AtomicReference<>(null);
        final CountDownLatch latch = new CountDownLatch(1);
        while (true) {
            List<WebClient> shouldCloseWebClient = new ArrayList<>();
            List<Proxy> usingProxy = new ArrayList<>();
            // 先一轮并发3个
            for (int i = 0; i < 3; i++) {
                // 从proxies中拿到一个代理，并设置给jsoupConnection
                final Proxy proxy1 = options.getUseProxy() ? proxyService.get(url.toExternalForm(), options.getAnonymity()) : null;
                if (proxy1 != null) {
                    usingProxy.add(proxy1);
                }
                WebClient webClient = newWebClient(proxy1);
                shouldCloseWebClient.add(webClient);
                try {
                    HttpRequest<Buffer> bufferHttpRequest = webClient.getAbs(url.toExternalForm()).timeout(TIMEOUT / 3);
                    request.headers().forEach(bufferHttpRequest::putHeader);
                    bufferHttpRequest.send(response -> {
                        if (response.succeeded() && StringUtils.isNotBlank(response.result().bodyAsString())) {
                            String body = ProxyUtil.decodeBody(response.result());
                            if (pattern.matcher(body).find()) {
                                html.set(body);
                                latch.countDown();
                            } else {
                                if (hasBlocked(body)) {
                                    // log.info("vertx web client has been blocked! proxy: {}, url: {}, regex: {}, response: {}", proxy1, url, regex, StringUtils.substring(body, 0, 1000));
                                    proxyService.remove(proxy1);
                                } else {
                                    // 此时链接访问正常，但是未返回期望的结果，
                                    // 有可能目标链接包含的内容确实已经发生更改，
                                    // 用户提供的regex未匹配结果是正常情况
                                    // 故跳出死循环
                                    if (retry.decrementAndGet() <= 0) {
                                        log.error("vertx web client check regex error, proxy: {}, url: {}, regex: {}, response: {}", proxy1, url, regex, body);
                                    }
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    proxyService.remove(proxy1);
                }
            }
            try {
                latch.await(TIMEOUT / 3, TimeUnit.MILLISECONDS);
                // close webClient then can release pool connections
                shouldCloseWebClient.forEach(WebClient::close);
                shouldCloseWebClient.clear();
                // takeDecr usingProxy
                usingProxy.forEach(proxy1 -> proxyService.takeDecr(proxy1));
                usingProxy.clear();

                if (html.get() != null) {
                    return html.get();
                }
                if (retry.get() <= 0) {
                    return null;
                }
            } catch (Exception e) {
                log.error("vertx web client error: {}, url: {}", e.getMessage(), url);
            }
        }
    }

    /**
     * 提供一个便捷的静态方法获取使用代理的 Jsoup Get
     *
     * @param url   目标网址
     * @param regex response 校验正则，不符合预期的将被循环执行
     */
    public static Document jsoupGet(String url, String regex) {
        long start = System.currentTimeMillis();
        if (true) {
            Document document = jsoupGet(Jsoup.connect(url), regex);
            long end = System.currentTimeMillis();
            log.info("JsoupDownloader.download elapse: {}s, url: {}", (end - start) / 1000, url);
            return document;
        } else {
            String html = vertxDownload(Jsoup.connect(url), regex, null);
            long end = System.currentTimeMillis();
            log.info("JsoupDownloader.vertxDownload elapse: {}s, url: {}", (end - start) / 1000, url);

            if (html == null) {
                return null;
            }
            Document document = null;
            try {
                document = Jsoup.parse(html, url);
            } catch (Exception e) {
                log.error("JsoupDownloader.vertxDownload jsoup.parse error: {}", e.getMessage());
            }
            return document;
        }
    }

    /**
     * 提供一个便捷的静态方法获取使用代理的 Jsoup Get
     *
     * @param connection 指定了url以及一些参数的 jsoup Connection
     * @param regex      response 校验正则，不符合预期的将被循环执行
     */
    public static Document jsoupGet(Connection connection, String regex) {
        Connection.Response response = jsoupExecute(connection, regex);
        if (response != null) {
            try {
                return response.parse();
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    /**
     * 提供一个便捷的静态方法获取使用代理的 Jsoup Execute
     *
     * @param url   目标网址
     * @param regex response 校验正则，不符合预期的将被循环执行
     */
    public static Connection.Response jsoupExecute(String url, String regex) {
        Connection connect = Jsoup.connect(url);
        return download(connect, regex, null);
    }

    /**
     * 提供一个便捷的静态方法获取使用代理的 Jsoup Execute
     *
     * @param jsoupConnection 指定了url以及一些参数的 jsoup Connection
     * @param regex           response 校验正则，不符合预期的将被循环执行
     */
    public static Connection.Response jsoupExecute(Connection jsoupConnection, String regex) {
        return download(jsoupConnection, regex, null);
    }
}