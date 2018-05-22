package com.danyl.springbootsell;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@MapperScan(basePackages = "com.danyl.springbootsell.entity.mapper")
public class SpringbootsellApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootsellApplication.class, args);
    }
}
