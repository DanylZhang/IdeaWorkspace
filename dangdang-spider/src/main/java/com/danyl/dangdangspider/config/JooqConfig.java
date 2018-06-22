package com.danyl.dangdangspider.config;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;

@org.springframework.context.annotation.Configuration
public class JooqConfig {

    @Autowired
    @Qualifier("dataSourceProxy")
    private DataSource dataSourceProxy;

    @Autowired
    @Qualifier("dataSourceDangDang")
    private DataSource dataSourceDangDang;

    @Autowired
    @Qualifier("dataSourceH2Test")
    private DataSource dataSourceH2Test;

    @Bean(name = "jooqConfigurationProxy")
    public Configuration configurationProxy() {
        TransactionAwareDataSourceProxy transactionAwareDataSourceProxy = new TransactionAwareDataSourceProxy(dataSourceProxy);
        DataSourceConnectionProvider dataSourceConnectionProvider = new DataSourceConnectionProvider(transactionAwareDataSourceProxy);
        return new DefaultConfiguration()
                .set(dataSourceConnectionProvider)
                .set(SQLDialect.SQLITE);
    }

    @Bean(name = "DSLContextProxy")
    public DSLContext getDSLContextProxy() {
        return DSL.using(configurationProxy());
    }

    @Bean(name = "jooqConfigurationDangDang")
    public Configuration configurationDangDang() {
        TransactionAwareDataSourceProxy transactionAwareDataSourceProxy = new TransactionAwareDataSourceProxy(dataSourceDangDang);
        DataSourceConnectionProvider dataSourceConnectionProvider = new DataSourceConnectionProvider(transactionAwareDataSourceProxy);
        return new DefaultConfiguration()
                .set(dataSourceConnectionProvider)
                .set(SQLDialect.MYSQL_5_7);
    }

    @Bean(name = "DSLContextDangDang")
    public DSLContext getDSLContextDangDang() {
        return DSL.using(configurationDangDang());
    }

    @Bean(name = "jooqConfigurationH2Test")
    public Configuration configurationH2Test() {
        TransactionAwareDataSourceProxy transactionAwareDataSourceProxy = new TransactionAwareDataSourceProxy(dataSourceH2Test);
        DataSourceConnectionProvider dataSourceConnectionProvider = new DataSourceConnectionProvider(transactionAwareDataSourceProxy);
        return new DefaultConfiguration()
                .set(dataSourceConnectionProvider)
                .set(SQLDialect.H2);
    }

    @Bean(name = "DSLContextH2Test")
    public DSLContext getDSLContextH2Test() {
        return DSL.using(configurationH2Test());
    }
}
