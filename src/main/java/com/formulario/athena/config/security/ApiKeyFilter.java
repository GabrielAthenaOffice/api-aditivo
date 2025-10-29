package com.formulario.athena.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class ApiKeyFilter extends OncePerRequestFilter {

    @Value("${api.security.service.key}")
    private String validApiKey;

    private static final String API_KEY_HEADER = "X-API-Key";
    private static final String INTERNAL_PATH = "/api/aditivos/internal/";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Aplica apenas para rotas internas
        if (request.getRequestURI().startsWith(INTERNAL_PATH)) {
            String apiKey = request.getHeader(API_KEY_HEADER);

            log.info("🔐 Verificando API Key para: {}", request.getRequestURI());
            log.info("🔑 API Key recebida: {}", apiKey != null ? "***" + apiKey.substring(Math.max(0, apiKey.length() - 4)) : "NULA");

            if (validApiKey.equals(apiKey)) {
                // Autentica como serviço interno
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken("correspondencias-service", null,
                                List.of(new SimpleGrantedAuthority("ROLE_SERVICE")));
                SecurityContextHolder.getContext().setAuthentication(auth);

                log.info("✅ API Key válida - Autenticado como serviço");
            } else {
                log.warn("❌ API Key inválida ou ausente");
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.getWriter().write("{\"error\": \"API Key inválida\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Aplica APENAS para rotas internas, ignora todas as outras
        return !request.getRequestURI().startsWith(INTERNAL_PATH);
    }
}
