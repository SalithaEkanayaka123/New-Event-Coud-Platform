package com.newevent.registrationservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Explicitly allowlists known frontend origins rather than wildcarding "*" -
 * a small but real security-conscious choice worth mentioning alongside the
 * other least-privilege decisions (IAM scoping, ClickHouse insert-only user).
 *
 * Add the real cluster frontend URL here once it exists (Day 12) -
 * e.g. registry.addMapping("/api/**").allowedOrigins("http://localhost:8080", "http://<elastic-ip>")
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:8080")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
