package com.danyl.springbootconfiguration.logback;

import ch.qos.logback.classic.BasicConfigurator;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

public class LogbackDemo {
    public static void main(String[] args) {
        LoggerContext loggerContext = new LoggerContext();
        Logger logger = loggerContext.getLogger(LogbackDemo.class);
        BasicConfigurator basicConfigurator = new BasicConfigurator();
        basicConfigurator.configure(loggerContext);
        logger.info("hello world");
    }
}