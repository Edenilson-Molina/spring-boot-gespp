package com.template.spring_mvc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Sirve archivos del directorio "uploads" del working directory a través de /uploads/**
    Path uploadsDir = Path.of("uploads").toAbsolutePath().normalize();
    String location = uploadsDir.toUri().toString(); // e.g. file:///C:/.../uploads/
    registry.addResourceHandler("/uploads/**")
        .addResourceLocations(location)
        .setCachePeriod(0);
    }
}
