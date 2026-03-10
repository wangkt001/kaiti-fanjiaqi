package com.campus.yujianhaowu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 安全配置
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用 CSRF
            .csrf(AbstractHttpConfigurer::disable)
            // 禁用 Session
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 禁用表单登录
            .formLogin(AbstractHttpConfigurer::disable)
            // 禁用 HTTP Basic
            .httpBasic(AbstractHttpConfigurer::disable)
            // 配置请求授权
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // 放行所有请求，使用我们自己的拦截器
            );
        
        return http.build();
    }
}
