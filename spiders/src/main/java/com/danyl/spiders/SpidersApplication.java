package com.danyl.spiders;

import com.danyl.spiders.utils.ProxyUtil;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.ext.web.client.WebClient;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

@Slf4j
@SpringBootApplication
public class SpidersApplication {
    public static void main(String[] args) {
//        testVertx();
//        testJsoup();
        SpringApplication.run(SpidersApplication.class, args);
    }

    public static void testJsoup() {
        try {
            Document document = Jsoup.connect("http://www.ip3366.net/free/?stype=1&page=5")
//                    .proxy("191.252.201.100", 8080)
                    .get();
            System.out.println(document.html());
            System.out.println(document.charset().toString());
            System.out.println("111");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public static void testVertx() {
        VertxOptions vertxOptions = new VertxOptions();
        vertxOptions.setWorkerPoolSize(4);
        WebClient webClient = WebClient.create(Vertx.vertx(vertxOptions));
        CountDownLatch latch = new CountDownLatch(1);
        webClient.getAbs("http://www.ip3366.net/free/?stype=1&page=5")
                .send(response -> {
                    if (response.succeeded()) {
                        String body = ProxyUtil.decodeBody(response.result());
                        System.out.println(body);
                    }
                    latch.countDown();
                });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}