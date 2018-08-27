package com.danyl.spiders.downloader;

import com.danyl.spiders.config.WebDriverConfig;
import com.danyl.spiders.jooq.gen.proxy.tables.pojos.Proxy;
import com.danyl.spiders.service.ProxyService;
import com.danyl.spiders.utils.ProxyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.danyl.spiders.constants.TimeConstants.MINUTES;
import static com.danyl.spiders.constants.TimeConstants.TIMEOUT;

@Slf4j
@Service
public class PhantomJSDownloader implements WebDriverDownloader {
    private ProxyService proxyService = ProxyService.getInstance();

    @Autowired
    private WebDriverConfig webDriverConfig;

    public String PhantomJSExecute(String url, By by, String regex, Boolean useProxy) {
        webDriverConfig.getDriverMap()
                .forEach((key, value) -> {
                    File file = new File(value);
                    if (file.exists()) {
                        System.setProperty(key, value);
                    } else {
                        log.error("file not exists: {}", value);
                    }
                });

        // 链接访问正常，但返回未匹配数据时的重试次数
        int count = 3;
        Pattern pattern = Pattern.compile(regex);
        while (true) {
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setJavascriptEnabled(true);
            capabilities.setCapability("takesScreenshot", true);
            capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX,"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");

            // 从proxies中拿到一个代理，并设置给chromeOptions
            Proxy proxy0 = null;
            if (useProxy) {
                proxy0 = proxyService.get(url);
            }
            if (proxy0 != null) {
                capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, ProxyUtil.getPhantomJSProxy(proxy0));
            }

            RemoteWebDriver webDriver = null;
            try {
                long start = System.currentTimeMillis();

                try {
                    webDriver = new PhantomJSDriver(capabilities);
                    webDriver.manage().window().setSize(new Dimension(1366, 768));
                    webDriver.manage().timeouts().pageLoadTimeout(MINUTES, TimeUnit.MILLISECONDS);
                } catch (Exception e) {
                    log.error("new PhantomJSDriver error: {}", e.getMessage());
                    return null;
                }

                webDriver.get(url);
                String pageSource = webDriver.getPageSource();
                long end = System.currentTimeMillis();
                log.info("phantomjs execute get elapse: {}s", (end - start) / 1000);

                if (pattern.matcher(pageSource).find()) {
                    return pageSource;
                } else {
                    // 此时链接访问正常，但是未返回期望的结果，
                    // 有可能目标链接包含的内容确实已经发生更改，
                    // 用户提供的regex未匹配结果是正常情况
                    // 故跳出死循环
                    if (count-- <= 0) {
                        log.error("phantomjs check selector error, url: {}, by: {}, response: {}", url, by, webDriver.getTitle());
                        return null;
                    }
                }
            } catch (Exception e) {
                log.error("phantomjs error: {}, url: {}, proxy: {}", e.getMessage(), url, proxy0);
                proxyService.remove(proxy0);
            } finally {
                if (webDriver != null) {
                    webDriver.quit();
                }
            }
        }
    }

    public String screenShot(String url, String path, By by, String regex, Boolean useProxy) {
        webDriverConfig.getDriverMap().forEach((key, value) -> {
            File file = new File(value);
            if (file.exists()) {
                System.setProperty(key, value);
            } else {
                log.error("file not exists: {}", value);
            }
        });

        // 链接访问正常，但返回未匹配数据时的重试次数
        int count = 3;
        Pattern pattern = Pattern.compile(regex);
        while (true) {
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setJavascriptEnabled(true);
            capabilities.setCapability("takesScreenshot", true);

            // 从proxies中拿到一个代理，并设置给chromeOptions
            Proxy proxy0 = null;
            if (useProxy) {
                proxy0 = proxyService.get(url);
            }
            if (proxy0 != null) {
                capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, ProxyUtil.getPhantomJSProxy(proxy0));
            }

            RemoteWebDriver webDriver = null;
            try {
                long start = System.currentTimeMillis();

                try {
                    webDriver = new PhantomJSDriver(capabilities);
                    webDriver.manage().window().setSize(new Dimension(1366, 768));
                    webDriver.manage().timeouts().pageLoadTimeout(MINUTES, TimeUnit.MILLISECONDS);
                } catch (Exception e) {
                    log.error("new PhantomJSDriver error: {}", e.getMessage());
                    return null;
                }

                webDriver.get(url);
                String pageSource = webDriver.getPageSource();
                long end = System.currentTimeMillis();
                log.info("chrome execute get elapse: {}s", (end - start) / 1000);

                if (pattern.matcher(pageSource).find()) {
                    WebElement element = webDriver.findElement(by);
                    // Get entire page screenshot
                    File screenshot = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
                    BufferedImage fullImg = null;
                    try {
                        fullImg = ImageIO.read(screenshot);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Get the location of element on the page
                    Point point = element.getLocation();

                    // Get width and height of the element
                    int elementWidth = element.getSize().getWidth();
                    int elementHeight = element.getSize().getHeight();

                    // Crop the entire page screenshot to get only element screenshot
                    BufferedImage eleScreenshot = fullImg.getSubimage(point.getX(), point.getY(),
                            elementWidth, elementHeight);
                    try {
                        ImageIO.write(eleScreenshot, "png", screenshot);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Copy the element screenshot to disk
                    File screenshotLocation = new File(path);
                    try {
                        FileUtils.copyFile(screenshot, screenshotLocation);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    webDriver.close();
                } else {
                    // 此时链接访问正常，但是未返回期望的结果，
                    // 有可能目标链接包含的内容确实已经发生更改，
                    // 用户提供的regex未匹配结果是正常情况
                    // 故跳出死循环
                    if (count-- <= 0) {
                        log.error("phantomjs check selector error, url: {}, by: {}, response: {}", url, by, webDriver.getTitle());
                        return null;
                    }
                }
            } catch (Exception e) {
                // log.error("jsoupExecute error: {}, url: {}, proxy: {}", e.getMessage(), url, proxy0);
                proxyService.remove(proxy0);
            } finally {
                if (webDriver != null) {
                    webDriver.quit();
                }
            }
        }
    }
}