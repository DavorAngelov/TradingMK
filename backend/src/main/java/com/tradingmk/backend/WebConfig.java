package com.tradingmk.backend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5175", "http://localhost:5176","http://localhost:5173")
                .allowedMethods("*")
                .allowedHeaders("*").allowCredentials(true);
    }
}
