package com.formulario.athena.documents;

import com.formulario.athena.config.exceptions.APIExceptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

//@Component
@Slf4j
public class TemplateVerifier {

    @PostConstruct
    public void verificarTemplate() {
        try {
            ClassPathResource resource = new ClassPathResource("templates/AditivoContratual.docx");
            boolean templateExiste = resource.exists();

            log.info("Template existe: {}", templateExiste);

            if (templateExiste) {
                // ✅ Testa se consegue ler o arquivo
                try (InputStream is = resource.getInputStream()) {
                    log.info("✅ Template pode ser lido com sucesso");
                }
            } else {
                log.error("❌ TEMPLATE NÃO ENCONTRADO!");
            }

        } catch (Exception e) {
            log.error("Erro ao verificar template: {}", e.getMessage());
        }
    }
}