package com.xw.config;

import com.xw.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 全局配置类
 * @author XW
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    // 注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/**") // 拦截所有 /api 下的请求
                .excludePathPatterns(       // 排除不需要登录的接口
                        "/api/auth/login",
                        "/api/auth/register",
                        "/api/auth/resetPassword",
                        "/api/admin/login",
                        // 排除 Swagger 接口文档路径
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
                );
    }

    // 静态资源映射 (保留你原有的代码)
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = System.getProperty("user.dir") + "/uploads/";
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath);
    }
}