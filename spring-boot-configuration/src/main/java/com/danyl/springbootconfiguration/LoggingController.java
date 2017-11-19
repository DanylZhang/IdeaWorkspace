package com.danyl.springbootconfiguration;

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoggingController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/log")
    public void log(@RequestParam String message) {
        if (logger instanceof ch.qos.logback.classic.Logger) {
            ch.qos.logback.classic.Logger.class.cast(logger).setLevel(Level.DEBUG);
        }
        logger.debug(message);
    }
}