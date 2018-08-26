package com.danyl.spiders;

import com.google.common.io.Files;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.Command;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class SpidersApplication {
    public static void main(String[] args) {
        test();
        SpringApplication.run(SpidersApplication.class, args);
    }

    public static void test() {

        System.setProperty("webdriver.chrome.driver", "C:/Users/DELL/AppData/Local/360Chrome/Chrome/Application/chromedriver.exe");
        System.setProperty("phantomjs.binary.path", "D:/360极速浏览器下载/phantomjs-2.1.1-windows/bin/phantomjs.exe");

        PhantomJSDriver phantomJSDriver = new PhantomJSDriver();
        phantomJSDriver.manage().window().setSize(new Dimension(1920,1080));
        phantomJSDriver.get("https://www.vip.com/");
        phantomJSDriver.executeScript("window.scrollTo(0,1000)");
        try {
            Thread.sleep(15*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(phantomJSDriver.getTitle());
        try {
            Files.copy(phantomJSDriver.getScreenshotAs(OutputType.FILE),new File("D:/1.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        WebElement elementByCssSelector1 = phantomJSDriver.findElementByCssSelector("#J_slideBanner_panel");
        String outerHTML = elementByCssSelector1.getAttribute("outerHTML");
        System.out.println(outerHTML);
        try {
            Files.copy(elementByCssSelector1.getScreenshotAs(OutputType.FILE),new File("D:/2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        phantomJSDriver.close();
        phantomJSDriver.quit();
        System.exit(0);

        org.openqa.selenium.Proxy proxy = new org.openqa.selenium.Proxy();
        // 193.109.239.22:21231
        proxy.setHttpProxy("193.109.239.22:21231");
        // 针对https的代理
        proxy.setSslProxy("193.109.239.22:21231");
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setProxy(proxy);
        ChromeDriver chromeDriver = new ChromeDriver(chromeOptions);
        chromeDriver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);

        long start = System.currentTimeMillis();
        try {
            chromeDriver.get("https://www.baidu.com/s?wd=ip&ie=UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("elapse " + (end - start) / 1000);

        String title = chromeDriver.getTitle();

        System.out.println(title);

        try {
            chromeDriver.get("https://proxydb.net/?offset=15");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(chromeDriver.getTitle());
        WebElement elementByCssSelector = chromeDriver.findElementByCssSelector("body > div > div.table-responsive > table > tbody > tr:nth-child(1) > td:nth-child(1) > a");
        String text = elementByCssSelector.getText();
        System.out.println(text);

        chromeDriver.close();
        // finally quit
        chromeDriver.quit();

        System.exit(0);
    }

    public void test2(){
//        driver.get("http://www.google.com");
//        WebElement ele = driver.findElement(By.id("hplogo"));
//
//// Get entire page screenshot
//        File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
//        BufferedImage  fullImg = ImageIO.read(screenshot);
//
//// Get the location of element on the page
//        Point point = ele.getLocation();
//
//// Get width and height of the element
//        int eleWidth = ele.getSize().getWidth();
//        int eleHeight = ele.getSize().getHeight();
//
//// Crop the entire page screenshot to get only element screenshot
//        BufferedImage eleScreenshot= fullImg.getSubimage(point.getX(), point.getY(),
//                eleWidth, eleHeight);
//        ImageIO.write(eleScreenshot, "png", screenshot);
//
//// Copy the element screenshot to disk
//        File screenshotLocation = new File("C:\\images\\GoogleLogo_screenshot.png");
//        FileUtils.copyFile(screenshot, screenshotLocation);
    }
}