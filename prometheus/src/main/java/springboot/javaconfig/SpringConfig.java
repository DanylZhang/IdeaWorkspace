package springboot.javaconfig;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration //通过该注解表明该类是一个Spring的配置，相当于一个xml文件
@ComponentScan(basePackages = "springboot.javaconfig") //配置扫描包
@PropertySource(value = {"classpath:db.properties"}, ignoreResourceNotFound = true)
public class SpringConfig {
    @Bean //通过该注解表明是一个Bean对象，相当于xml中的<Bean>
    public UserDAO getUserDAO() {
        return new UserDAO();
    }

    @Value("${jdbc.driver}")
    private String jdbcDriverClassName;

    @Value("${jdbc.url}")
    private String jdbcUrl;

    @Value(("${jdbc.username}"))
    private String jdbcUserName;

    @Value("${jdbc.password}")
    private String jdbcPassword;

    @Bean(destroyMethod = "close")
    public DruidDataSource druidDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(this.jdbcDriverClassName);
        druidDataSource.setUrl(this.jdbcUrl);
        druidDataSource.setUsername(this.jdbcUserName);
        druidDataSource.setPassword(this.jdbcPassword);
        return druidDataSource;
    }
}