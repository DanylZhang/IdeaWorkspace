package com.danyl.spiders;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class SpidersApplication {
    public static void main(String[] args) {
        //test();
        SpringApplication.run(SpidersApplication.class, args);
    }

    public static void test() {

        System.setProperty("webdriver.chrome.driver", "C:/Users/DELL/AppData/Local/360Chrome/Chrome/Application/chromedriver.exe");

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
}