package com.bmsedge.asset.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS Configuration for Asset Management API
 *
 * This fixes the "Failed to fetch" error by allowing requests from your React frontend
 */
@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        // Allow requests from these origins (your React dev servers)
                        .allowedOrigins(
                                "http://localhost:3000",      // Create React App default
                                "http://localhost:5173",      // Vite default
                                "http://localhost:5174",      // Vite alternative
                                "http://127.0.0.1:3000",
                                "http://127.0.0.1:5173",
                                "http://127.0.0.1:5174"
                        )
                        // Allow these HTTP methods
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                        // Allow all headers (including Authorization)
                        .allowedHeaders("*")
                        // Allow credentials (cookies, authorization headers)
                        .allowCredentials(true)
                        // How long to cache preflight requests
                        .maxAge(3600);
            }
        };
    }
}