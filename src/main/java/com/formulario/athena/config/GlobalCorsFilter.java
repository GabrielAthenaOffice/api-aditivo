package com.formulario.athena.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class GlobalCorsFilter {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // ✅ PERMITE todas as origens (ou especifique as que precisa)
        config.addAllowedOrigin("*");
        // Ou especifique:
        // config.setAllowedOrigins(Arrays.asList(
        //     "https://front-correspondencias-athena.vercel.app",
        //     "http://localhost:5173",
        //     "http://127.0.0.1:5173"
        // ));

        // ✅ PERMITE todos os métodos
        config.addAllowedMethod("*");

        // ✅ PERMITE todos os headers
        config.addAllowedHeader("*");

        // ✅ EXPÕE os headers necessários para download
        config.setExposedHeaders(Arrays.asList(
                "Content-Disposition",
                "Content-Length",
                "Content-Type"
        ));

        // ✅ IMPORTANTE: Para requests com credenciais, mas você disse allowCredentials=false
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}