package com.manage.cattle.config;

import com.manage.cattle.interceptor.JWTInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Resource
    private JWTInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                //拦截所有请求
                .addPathPatterns("/**")
                //不需要拦截的请求
                .excludePathPatterns("/user/login", "/rest/**");
    }

}
