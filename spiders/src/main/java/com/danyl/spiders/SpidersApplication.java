package com.danyl.spiders;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class SpidersApplication {
    public static void main(String[] args) {
//        test();
        SpringApplication.run(SpidersApplication.class, args);
    }

    public static void test() {

        System.exit(0);
    }
}