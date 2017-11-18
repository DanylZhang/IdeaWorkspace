package com.danyl.springbootconfiguration.slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jDemo {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(Slf4jDemo.class);
        logger.info("hello world");
    }
}