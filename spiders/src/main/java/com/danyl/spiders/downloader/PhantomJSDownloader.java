package com.danyl.spiders.downloader;

import com.danyl.spiders.jooq.gen.proxy.tables.pojos.Proxy;
import com.danyl.spiders.service.ProxyService;
import com.danyl.spiders.utils.ProxyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.*;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import static com.danyl.spiders.constants.Constants.USERAGENT;
import static com.danyl.spiders.constants.TimeConstants.MINUTES;

@Slf4j
@Service
public class PhantomJSDownloader implements WebDriverDownloader {
    private ProxyService proxyService = ProxyService.getInstance();

    private WebDriver webDriver;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Thread processThread;

    public PhantomJSDownloader() {
        webDriver = newWebDriver(null);
    }

    private WebDriver newWebDriver(Proxy proxy) {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setBrowserName("chrome");
        capabilities.setVersion("63");
        capabilities.setPlatform(Platform.WIN10);
        capabilities.setJavascriptEnabled(true);
        capabilities.setAcceptInsecureCerts(true);
        capabilities.setCapability("takesScreenshot", true);
        capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "userAgent", USERAGENT);
        capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "loadImages", true);

        if (proxy != null) {
            capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, ProxyUtil.getPhantomJSProxy(proxy));
        }

        WebDriver webDriver = new PhantomJSDriver(capabilities);
        webDriver.manage().window().setSize(new Dimension(1366, 768));
        return webDriver;
    }

    public WebDriver getWebDriver() {
        return webDriver;
    }

    public Boolean get(String url, Boolean useProxy, long timeout) {
        if (useProxy) {
            doSetProxy(proxyService.get(url));
        }

        AtomicBoolean success = new AtomicBoolean(false);
        final CountDownLatch latch = new CountDownLatch(1);
        executor.execute(() -> {
            processThread = Thread.currentThread();
            try {
                webDriver.get(url);
                success.set(true);
            } catch (Exception e) {
                log.error("PhantomJSDownloader get error: {}", e.getMessage());
            } finally {
                latch.countDown();
            }
        });
        try {
            latch.await(timeout, TimeUnit.MILLISECONDS);
        } catch (Exception ignored) {
        }
        return success.get();
    }

    public void setValue(String selector, String value) {
        setValue(By.cssSelector(selector), value, 1000);
    }

    public void setValue(By by, String value, long waitTime) {
        WebElement element = webDriver.findElement(by);
        if (element != null) {
            element.sendKeys(value);
            try {
                Thread.sleep(waitTime);
            } catch (Exception ignored) {
            }
        }
    }

    public void click(String selector) {
        click(By.cssSelector(selector), 1000);
    }

    public void click(By by, long waitTime) {
        WebElement element = webDriver.findElement(by);
        if (element != null) {
            element.click();
            try {
                Thread.sleep(waitTime);
            } catch (Exception ignored) {
            }
        }
    }

    public void clearCookie() {
        webDriver.manage().deleteAllCookies();
    }

    public void doSetProxy(Proxy proxy) {
        if (webDriver != null) {
            webDriver.close();
            webDriver.quit();
        }
        webDriver = newWebDriver(proxy);
    }

    public void insideClose() {
        try {
            processThread.interrupt();
            webDriver.close();
            webDriver.quit();
            if (executor != null) {
                executor.shutdown();
            }
        } catch (Exception e) {
            log.error("PhantomJSDownloader insideClose error: {}", e.getMessage());
        }
    }

    public static String download(String url, String regex, DownloaderOptions options) {
        ProxyService proxyService = ProxyService.getInstance();
        Pattern pattern = Pattern.compile(regex);
        if (options == null) {
            options = new DownloaderOptions(true, "L2");
        }

        // 链接访问正常，但返回未匹配数据时的重试次数
        int retry = 3;
        while (true) {
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setBrowserName("chrome");
            capabilities.setVersion("63");
            capabilities.setPlatform(Platform.WIN10);
            capabilities.setJavascriptEnabled(true);
            capabilities.setAcceptInsecureCerts(true);
            capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "userAgent", USERAGENT);
            capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "loadImages", false);

            // 从proxies中拿到一个代理，并设置给capabilities
            Proxy proxy0 = null;
            if (options.getUseProxy()) {
                proxy0 = proxyService.get(url, options.getAnonymity());
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
                    webDriver.manage().timeouts().pageLoadTimeout(MINUTES * 5, TimeUnit.MILLISECONDS);
                } catch (Exception e) {
                    log.error("new PhantomJSDriver error: {}", e.getMessage());
                    return null;
                }

                webDriver.get(url);
                String pageSource = webDriver.getPageSource();
                long end = System.currentTimeMillis();
                log.info("PhantomJSDownloader get elapse: {}s", (end - start) / 1000);

                if (pattern.matcher(pageSource).find()) {
                    return pageSource;
                } else {
                    // 此时链接访问正常，但是未返回期望的结果，
                    // 有可能目标链接包含的内容确实已经发生更改，
                    // 用户提供的regex未匹配结果是正常情况
                    // 故跳出死循环
                    if (retry-- <= 0) {
                        log.error("PhantomJSDownloader check regex error, url: {}, response: {}", url, webDriver.getTitle());
                        return null;
                    }
                }
            } catch (Exception e) {
                log.error("PhantomJSDownloader error: {}, url: {}, proxy: {}", e.getMessage(), url, proxy0);
                proxyService.remove(proxy0);
            } finally {
                if (webDriver != null) {
                    webDriver.quit();
                }
            }
        }
    }

    public static String screenShot(String url, String path, By by, String regex, DownloaderOptions options) {
        ProxyService proxyService = ProxyService.getInstance();
        Pattern pattern = Pattern.compile(regex);
        if (options == null) {
            options = new DownloaderOptions(true, "L2");
        }

        // 链接访问正常，但返回未匹配数据时的重试次数
        int retry = 3;
        while (true) {
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setBrowserName("chrome");
            capabilities.setVersion("63");
            capabilities.setPlatform(Platform.WIN10);
            capabilities.setJavascriptEnabled(true);
            capabilities.setAcceptInsecureCerts(true);
            capabilities.setCapability("takesScreenshot", true);
            capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "userAgent", USERAGENT);
            capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "loadImages", true);

            // 从proxies中拿到一个代理，并设置给chromeOptions
            Proxy proxy0 = null;
            if (options.getUseProxy()) {
                proxy0 = proxyService.get(url, options.getAnonymity());
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
                    webDriver.manage().timeouts().pageLoadTimeout(MINUTES * 5, TimeUnit.MILLISECONDS);
                } catch (Exception e) {
                    log.error("new PhantomJSDriver error: {}", e.getMessage());
                    return null;
                }

                webDriver.get(url);
                String pageSource = webDriver.getPageSource();
                long end = System.currentTimeMillis();
                log.info("PhantomJSDownloader get elapse: {}s", (end - start) / 1000);

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
                    if (retry-- <= 0) {
                        log.error("PhantomJSDownloader check selector error, url: {}, by: {}, response: {}", url, by, webDriver.getTitle());
                        return null;
                    }
                }
            } catch (Exception e) {
                proxyService.remove(proxy0);
            } finally {
                if (webDriver != null) {
                    webDriver.quit();
                }
            }
        }
    }

    public static Document getDocument(String url, String regex) {
        String html = download(url, regex, null);
        if (StringUtils.isNotBlank(html)) {
            try {
                return Jsoup.parse(html, url);
            } catch (Exception e) {
                log.error("PhantomJSDownloader getDocument Jsoup parse error: {}", e.getMessage());
            }
        }
        return null;
    }
}