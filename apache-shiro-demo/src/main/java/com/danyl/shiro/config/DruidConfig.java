package com.danyl.shiro.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DruidConfig {

    @Primary
    @Bean(name = "dataSourceShiro")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSourceShiro() {
        return DruidDataSourceBuilder.create().build();
    }
}
