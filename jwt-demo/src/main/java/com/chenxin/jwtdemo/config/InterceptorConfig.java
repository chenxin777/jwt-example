package com.chenxin.jwtdemo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author fangchenxin
 * @description
 * @date 2024/7/3 23:25
 * @modify
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Resource
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/user/login", "/user/register");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 设置允许跨域访问的路径
        registry.addMapping("/**")
                // 设置允许跨域请求的域名
                .allowedOrigins("http://localhost:8000")
                // 是否允许证书
                .allowCredentials(true)
                // 设置允许的方法
                .allowedMethods("*")
                // 跨域允许时间
                .maxAge(3600);
    }
}
