package com.examsystem.intelligent_exam_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // ✅ 所有路径
                .allowedOrigins("*") // ✅ 所有前端来源（开发阶段可用）
                .allowedMethods("*") // ✅ 所有请求方法
                .allowedHeaders("*");
    }
}
