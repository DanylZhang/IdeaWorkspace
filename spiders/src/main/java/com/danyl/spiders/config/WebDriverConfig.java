package com.danyl.spiders.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "webdriver")
public class WebDriverConfig {
    private Map<String, String> driverMap;

    @PostConstruct
    public void init() {
        driverMap.forEach(System::setProperty);
    }
}