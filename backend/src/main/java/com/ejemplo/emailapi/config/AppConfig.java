package com.ejemplo.emailapi.config;

import com.sendgrid.SendGrid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig implements WebMvcConfigurer {

    @Value("${sendgrid.api-key}")
    private String apiKey;

    // Orígenes permitidos separados por coma (variable CORS_ORIGINS)
    @Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;

    @Bean
    public SendGrid sendGrid() {
        return new SendGrid(apiKey);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("POST", "OPTIONS")
                .allowedHeaders("Content-Type");
    }
}
