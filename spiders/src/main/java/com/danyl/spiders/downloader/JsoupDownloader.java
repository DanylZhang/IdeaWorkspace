package com.danyl.spiders.downloader;

import com.danyl.spiders.jooq.gen.proxy.tables.pojos.Proxy;
import com.danyl.spiders.service.ProxyService;
import com.danyl.spiders.utils.ProxyUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;

import java.util.regex.Pattern;

import static com.danyl.spiders.constants.TimeConstants.TIMEOUT;

@Slf4j
public class JsoupDownloader {

    private static ProxyService proxyService = ProxyService.getInstance();

    /**
     * 提供一个便捷的静态方法获取使用代理的 Jsoup Execute
     *
     * @param jsoupConnection 指定了url以及一些参数的 jsoup Connection
     * @param regex           response 校验正则，不符合预期的将被循环执行
     * @param useProxy        是否使用代理爬取
     * @return the jsoup execute response, maybe null when regex don't match
     */
    public static Connection.Response jsoupExecute(Connection jsoupConnection, String regex, Boolean useProxy) {
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
                proxy0 = proxyService.get(url);
            }
            if (proxy0 != null) {
                jsoupConnection.proxy(ProxyUtil.getProxy(proxy0));
            }
            try {
                Connection.Response execute = jsoupConnection.execute();
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
                proxyService.remove(proxy0);
            }
        }
    }
}
