package com.danyl.spiders.config;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;

@org.springframework.context.annotation.Configuration
public class JooqConfig {

    @Bean(name = "DSLContextProxy")
    public DSLContext getDSLContextProxy(@Qualifier("dataSourceProxy") DataSource dataSource) {
        TransactionAwareDataSourceProxy transactionAwareDataSourceProxy = new TransactionAwareDataSourceProxy(dataSource);
        DataSourceConnectionProvider dataSourceConnectionProvider = new DataSourceConnectionProvider(transactionAwareDataSourceProxy);
        Configuration configuration = new DefaultConfiguration()
                .set(dataSourceConnectionProvider)
                .set(SQLDialect.SQLITE);
        return DSL.using(configuration);
    }

    @Bean(name = "DSLContextDangDang")
    public DSLContext getDSLContextDangDang(@Qualifier("dataSourceDangDang") DataSource dataSource) {
        TransactionAwareDataSourceProxy transactionAwareDataSourceProxy = new TransactionAwareDataSourceProxy(dataSource);
        DataSourceConnectionProvider dataSourceConnectionProvider = new DataSourceConnectionProvider(transactionAwareDataSourceProxy);
        Configuration configuration = new DefaultConfiguration()
                .set(dataSourceConnectionProvider)
                .set(SQLDialect.MYSQL_5_7);
        return DSL.using(configuration);
    }

    @Bean(name = "DSLContextNewVip")
    public DSLContext getDSLContextNewVip(@Qualifier("dataSourceNewVip") DataSource dataSource) {
        TransactionAwareDataSourceProxy transactionAwareDataSourceProxy = new TransactionAwareDataSourceProxy(dataSource);
        DataSourceConnectionProvider dataSourceConnectionProvider = new DataSourceConnectionProvider(transactionAwareDataSourceProxy);
        Configuration configuration = new DefaultConfiguration()
                .set(dataSourceConnectionProvider)
                .set(SQLDialect.MYSQL_5_7);
        return DSL.using(configuration);
    }

    @Bean(name = "DSLContextH2Test")
    public DSLContext getDSLContextH2Test(@Qualifier("dataSourceH2Test") DataSource dataSource) {
        TransactionAwareDataSourceProxy transactionAwareDataSourceProxy = new TransactionAwareDataSourceProxy(dataSource);
        DataSourceConnectionProvider dataSourceConnectionProvider = new DataSourceConnectionProvider(transactionAwareDataSourceProxy);
        Configuration configuration = new DefaultConfiguration()
                .set(dataSourceConnectionProvider)
                .set(SQLDialect.H2);
        return DSL.using(configuration);
    }
}
