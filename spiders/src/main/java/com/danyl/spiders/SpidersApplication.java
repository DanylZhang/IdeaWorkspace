package com.danyl.spiders;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@Slf4j
@SpringBootApplication
public class SpidersApplication {
    public static void main(String[] args) {
//        test();
        SpringApplication.run(SpidersApplication.class, args);
    }

    public static void test() {
        try {
            Document document = Jsoup.connect("http://proxydb.net/anon")
                    .proxy("191.252.201.100", 8080).get();
            System.out.println(document.html());
            System.out.println(document.charset().toString());
            System.out.println("111");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}