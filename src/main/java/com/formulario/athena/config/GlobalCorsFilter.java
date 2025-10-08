package com.formulario.athena.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class GlobalCorsFilter {


    @Bean
    public FilterRegistrationBean<org.springframework.web.filter.CorsFilter> corsFilter() {
        var cfg = new org.springframework.web.cors.CorsConfiguration();
        cfg.setAllowCredentials(false);
        cfg.setAllowedOriginPatterns(java.util.List.of(
                "https://front-correspondencias-athena.vercel.app",
                "http://localhost:*",
                "http://127.0.0.1:*"
        ));
        cfg.setAllowedMethods(java.util.List.of("GET","POST","PUT","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(java.util.List.of("*"));
        cfg.setExposedHeaders(java.util.List.of("Content-Disposition","Content-Length","Content-Type"));

        var src = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);

        var bean = new org.springframework.boot.web.servlet.FilterRegistrationBean<>(new org.springframework.web.filter.CorsFilter(src));
        bean.setOrder(org.springframework.core.Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}

