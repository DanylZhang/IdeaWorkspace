package com.danyl.xunwu.config;

import com.danyl.xunwu.security.AuthProvider;
import com.danyl.xunwu.security.LoginAuthFailHandler;
import com.danyl.xunwu.security.LoginUrlEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * HTTP权限控制
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 资源访问权限
        http.headers()
                .frameOptions().sameOrigin()

                .and()
                .authorizeRequests()
                .antMatchers("/admin/login").permitAll() // 管理员登陆入口
                .antMatchers("/static/**").permitAll() // 静态资源
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/user/login").permitAll() // 用户登陆入口
                .antMatchers("/user/**").hasAnyRole("ADMIN", "USER")
                .antMatchers("/api/user/**").hasAnyRole("ADMIN", "USER")

                .and()
                .csrf().disable()
                .formLogin()
                .loginProcessingUrl("/login") // 配置角色登陆处理入口
                .failureHandler(authFailHandler())

                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/logout/page")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(urlEntryPoint())
                .accessDeniedPage("/403");
    }

    /**
     * 自定义认证策略
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider()).eraseCredentials(true);
    }

    @Bean
    public AuthProvider authProvider() {
        return new AuthProvider();
    }

    @Bean
    public LoginUrlEntryPoint urlEntryPoint() {
        return new LoginUrlEntryPoint("/user/login");
    }

    @Bean
    public LoginAuthFailHandler authFailHandler() {
        return new LoginAuthFailHandler(urlEntryPoint());
    }
}