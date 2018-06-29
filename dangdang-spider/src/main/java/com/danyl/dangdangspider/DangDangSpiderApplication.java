package com.danyl.dangdangspider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@SpringBootApplication
public class DangDangSpiderApplication {
    public static void main(String[] args) {
        SpringApplication.run(DangDangSpiderApplication.class, args);
    }
}