package com.xw.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 全局配置类
 * @author XW
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取我们在代码里定义的根目录物理路径
        String uploadPath = System.getProperty("user.dir") + "/uploads/";

        // 将网络请求路径 /uploads/** 映射到本地硬盘的绝对路径下
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath);
    }
}