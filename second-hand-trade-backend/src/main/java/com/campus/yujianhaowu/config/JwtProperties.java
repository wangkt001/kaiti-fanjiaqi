package com.campus.yujianhaowu.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT 配置属性
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT 密钥
     */
    private String secret;

    /**
     * Token 过期时间（毫秒）默认 7 天
     */
    private Long expiration;

    /**
     * Refresh Token 过期时间（毫秒）默认 30 天
     */
    private Long refreshExpiration;

    /**
     * Token 头部
     */
    private String header;

    /**
     * Token 前缀
     */
    private String prefix;
}
