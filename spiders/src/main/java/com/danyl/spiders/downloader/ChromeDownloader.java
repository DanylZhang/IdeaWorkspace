package com.danyl.spiders.downloader;

import com.danyl.spiders.jooq.gen.proxy.tables.pojos.Proxy;
import com.danyl.spiders.service.ProxyService;
import com.danyl.spiders.utils.ProxyUtil;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.danyl.spiders.constants.TimeConstants.TIMEOUT;

@Slf4j
public class ChromeDownloader implements WebDriverDownloader {
    private static ProxyService proxyService = ProxyService.getInstance();

    public String download(String url, By by, String regex, Boolean useProxy) {
        // 链接访问正常，但返回未匹配数据时的重试次数
        int count = 3;
        Pattern pattern = Pattern.compile(regex);
        while (true) {
            ChromeOptions chromeOptions = new ChromeOptions();

            // 从proxies中拿到一个代理，并设置给chromeOptions
            Proxy proxy0 = null;
            if (useProxy) {
                proxy0 = proxyService.get(url);
            }
            if (proxy0 != null) {
                chromeOptions.setProxy(ProxyUtil.getChromeProxy(proxy0));
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
                proxyService.remove(proxy0);
            } finally {
                if (chromeDriver != null) {
                    chromeDriver.quit();
                }
            }
        }
    }
}