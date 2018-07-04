package com.danyl.shiro.config;

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
    @Qualifier("dataSourceShiro")
    private DataSource dataSourceShiro;

    @Bean(name = "jooqConfigurationShiro")
    public Configuration configurationShiro() {
        TransactionAwareDataSourceProxy transactionAwareDataSourceProxy = new TransactionAwareDataSourceProxy(dataSourceShiro);
        DataSourceConnectionProvider dataSourceConnectionProvider = new DataSourceConnectionProvider(transactionAwareDataSourceProxy);
        return new DefaultConfiguration()
                .set(dataSourceConnectionProvider)
                .set(SQLDialect.SQLITE);
    }

    @Bean(name = "DSLContextShiro")
    public DSLContext getDSLContextProxy() {
        return DSL.using(configurationShiro());
    }
}
