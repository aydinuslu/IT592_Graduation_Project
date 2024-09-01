package com.bookstore.bookcatalogservice;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080", "http://74.248.84.174", "https://74.248.84.174", "http://10.0.144.46")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD")
                .allowCredentials(true);
    }
}
