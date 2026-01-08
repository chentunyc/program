package com.training.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Web配置
 * 配置静态资源映射
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    /**
     * 配置静态资源映射
     * 将静态资源路径映射到实际的文件上传目录
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将相对路径转换为绝对路径
        Path basePath = Paths.get(uploadPath);
        if (!basePath.isAbsolute()) {
            basePath = Paths.get(System.getProperty("user.dir")).resolve(uploadPath).normalize();
        }
        // 确保路径以/结尾
        String location = basePath.toAbsolutePath().toString().replace("\\", "/");
        if (!location.endsWith("/")) {
            location = location + "/";
        }

        // 头像资源映射
        registry.addResourceHandler("/avatars/**")
                .addResourceLocations("file:" + location + "avatars/");

        // 上传文件资源映射
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + location);

        // 图片资源映射
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + location + "images/");

        // 资源文件映射（视频、音频、文档、仿真等）
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("file:" + location + "resources/");

        // 封面图片映射
        registry.addResourceHandler("/covers/**")
                .addResourceLocations("file:" + location + "covers/");

        // 文档资源映射
        registry.addResourceHandler("/documents/**")
                .addResourceLocations("file:" + location + "documents/");
    }
}
