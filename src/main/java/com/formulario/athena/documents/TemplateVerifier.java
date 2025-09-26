package com.formulario.athena.documents;

import com.formulario.athena.config.exceptions.APIExceptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class TemplateVerifier {

    @PostConstruct
    public void verificarTemplate() {
        try {
            ClassPathResource resource = new ClassPathResource("templates/AditivoContratual.docx");
            boolean templateExiste = resource.exists();

            log.info("Template existe: {}", templateExiste);

            if (templateExiste) {
                try {
                    log.info("Caminho do template: {}", resource.getFile().getAbsolutePath());
                } catch (IOException e) {
                    log.warn("Não foi possível obter caminho absoluto (pode estar dentro do JAR)");
                }
            } else {
                log.error("❌ TEMPLATE NÃO ENCONTRADO!");
                log.error("Coloque o arquivo AditivoContratual.docx em: src/main/resources/templates/");
                // Não lança exceção, apenas loga o erro
            }

        } catch (Exception e) {
            log.error("Erro ao verificar template: {}", e.getMessage());
            // Não relança a exceção para não parar a aplicação
        }
    }
}