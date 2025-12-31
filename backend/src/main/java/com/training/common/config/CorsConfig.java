package com.training.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/**
 * 跨域配置
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "training.cors")
public class CorsConfig {

    /**
     * 允许跨域的域名
     */
    private List<String> allowedOrigins;

    /**
     * 允许的请求方法
     */
    private List<String> allowedMethods;

    /**
     * 允许的请求头
     */
    private List<String> allowedHeaders;

    /**
     * 是否允许携带凭证
     */
    private Boolean allowCredentials;

    /**
     * 预检请求缓存时间
     */
    private Long maxAge;

    /**
     * 跨域过滤器
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // 允许的域名
        if (allowedOrigins != null) {
            allowedOrigins.forEach(config::addAllowedOrigin);
        }

        // 允许的方法
        if (allowedMethods != null) {
            allowedMethods.forEach(config::addAllowedMethod);
        }

        // 允许的请求头
        if (allowedHeaders != null) {
            allowedHeaders.forEach(config::addAllowedHeader);
        }

        // 是否允许携带凭证
        if (allowCredentials != null) {
            config.setAllowCredentials(allowCredentials);
        }

        // 预检请求缓存时间
        if (maxAge != null) {
            config.setMaxAge(maxAge);
        }

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
