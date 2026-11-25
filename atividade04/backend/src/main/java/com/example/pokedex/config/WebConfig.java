package com.example.pokedex.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String origem;

    public WebConfig(@Value("${app.cors.origem}") String origem) {
        this.origem = origem;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] origens = origem.split(",");
        registry.addMapping("/api/**")
                .allowedOrigins(origens)
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}
