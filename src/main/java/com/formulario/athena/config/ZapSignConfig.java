package com.formulario.athena.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ZapSignConfig {

    @Value("${zapsign.api.url}")
    private String baseUrl;

    @Value("${zapsign.api.token}")
    private String apiToken;

    @Bean
    public WebClient zapSignWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Token " + apiToken)
                .build();
    }
}
