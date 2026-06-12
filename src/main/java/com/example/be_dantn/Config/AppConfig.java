package com.example.be_dantn.Config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class  AppConfig implements WebMvcConfigurer {

    @Bean
    ModelMapper modelMapper (){
        return new ModelMapper();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/v1/**") // Apply CORS to all /api/v1/** endpoints
                .allowedOrigins("*") // Allow all origins (for development)
                .allowedMethods("*") // Allow all HTTP methods (GET, POST, PUT, DELETE, PATCH, etc.)
                .allowedHeaders("*"); // Allow all headers
    }
}
