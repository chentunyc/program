package com.training.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置
 * 配置静态资源映射
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload.path:E:/uploads}")
    private String uploadPath;

    /**
     * 配置静态资源映射
     * 将/avatars/**映射到实际的文件上传目录
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 确保路径以/结尾
        String location = uploadPath.endsWith("/") ? uploadPath : uploadPath + "/";

        // 头像资源映射
        registry.addResourceHandler("/avatars/**")
                .addResourceLocations("file:" + location + "avatars/");

        // 上传文件资源映射
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + location);
    }
}
