package com.formulario.athena;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns(
                                "http://localhost:*",
                                "http://127.0.0.1:*",
                                "https://*.athenaoffice.com.br",  // ajuste pro teu domínio real
                                "https://*.vercel.app",           // se usa Vercel
                                "https://*.netlify.app"           // se usa Netlify
                        )
                        .allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
                        .allowedHeaders("*")
                        .exposedHeaders("Content-Disposition","Content-Length","Content-Type")
                        .allowCredentials(false)   // como você NÃO usa cookie/sessão, deixa false
                        .maxAge(3600);
            }
        };
    }
}
