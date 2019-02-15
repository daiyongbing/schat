package com.bing.schat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
            .loginPage("/login.html")
            .successForwardUrl("/chat/index.html")
            .failureForwardUrl("/login.html")

            .and()
            .authorizeRequests()
            .antMatchers("/login.html","/register.html","/css/**","/js/**", "/img/**","/fonts/**").permitAll()
            .anyRequest()
            .authenticated(); //所有接口都必须经过身份验证
    }
}
