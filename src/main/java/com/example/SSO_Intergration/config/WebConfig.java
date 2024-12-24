package com.example.SSO_Intergration.config;

import com.example.SSO_Intergration.auth.AuthenticationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private AuthenticationInterceptor authenticationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/**") // Áp dụng cho tất cả đường dẫn
                .excludePathPatterns(
                        "/checkLogin/**", // Loại trừ các đường dẫn
                        "/api/**",
                        "/errors",
                        "/asset/**",
                        "/public/**"
                );
    }



}
