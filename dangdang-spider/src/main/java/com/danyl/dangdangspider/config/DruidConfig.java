package com.danyl.dangdangspider.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DruidConfig {

    @Primary
    @Bean(name = "dataSourceProxy")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSourceProxy() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = "dataSourceDangDang")
    @ConfigurationProperties(prefix = "spring.datasource.druid.dangdang")
    public DataSource dataSourceDangDang() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = "dataSourceH2Test")
    @ConfigurationProperties("spring.datasource.druid.h2test")
    public DataSource dataSourceH2Test() {
        return DruidDataSourceBuilder.create().build();
    }
}
