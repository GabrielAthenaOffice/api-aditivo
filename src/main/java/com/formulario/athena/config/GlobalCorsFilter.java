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
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        var cfg = new CorsConfiguration();
        cfg.setAllowCredentials(false); // não usa cookie
        cfg.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "http://127.0.0.1:*",
                "https://front-correspondencias-athena.vercel.app",
                "https://*.athenaoffice.com.br" // se tiver domínio próprio
        ));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setExposedHeaders(List.of("Content-Disposition","Content-Length","Content-Type"));
        // 👇 explícito (melhor p/ preflight)
        cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));

        var src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);

        var bean = new FilterRegistrationBean<>(new CorsFilter(src));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}
