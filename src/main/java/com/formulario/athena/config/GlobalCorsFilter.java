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
        CorsConfiguration c = new CorsConfiguration();
        c.setAllowCredentials(false); // você não usa sessão/cookie
        c.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "http://127.0.0.1:*",
                "https://*.athenaoffice.com.br",   // ajuste pro domínio real do front
                "https://*.vercel.app",
                "https://*.netlify.app"
        ));
        c.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        c.setAllowedHeaders(List.of("*"));
        c.setExposedHeaders(List.of("Content-Disposition","Content-Length","Content-Type"));

        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", c);

        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(src));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE); // garante que roda antes de tudo
        return bean;
    }
}
