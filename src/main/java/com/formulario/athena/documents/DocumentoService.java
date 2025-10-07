package com.formulario.athena.documents;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.formulario.athena.model.AditivoContratual;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class DocumentoService {

    private static final String MODELO_PATH = "templates/AditivoContratual.docx";

    public byte[] gerarAditivoContratual(AditivoContratual aditivo) {
        try {
            ClassPathResource modeloResource = new ClassPathResource(MODELO_PATH);
            if (!modeloResource.exists()) {
                throw new FileNotFoundException("Template não encontrado no classpath: " + MODELO_PATH);
            }

            Map<String, Object> data = criarMapaDadosSimplificado(aditivo);
            log.info("Gerando documento. Placeholders: {}", data.keySet());

            Configure config = Configure.builder().build();

            // Fechamento garantido
            try (var in = modeloResource.getInputStream();
                 var template = XWPFTemplate.compile(in, config).render(data);
                 var bos = new ByteArrayOutputStream()) {

                template.write(bos);
                // template.close() desnecessário com try-with-resources

                byte[] bytes = bos.toByteArray();
                log.info("✅ DOCX gerado - tamanho: {} bytes", bytes.length);

                // Diagnóstico: verificar assinatura ZIP "PK"
                if (bytes.length >= 2) {
                    log.info(String.format("Magic bytes: %02X %02X", bytes[0], bytes[1]));
                }

                if (bytes.length == 0) {
                    throw new IllegalStateException("Documento gerado com 0 bytes");
                }
                if (!(bytes.length >= 2 && bytes[0] == 0x50 && bytes[1] == 0x4B)) {
                    log.warn("DOCX sem assinatura PK. Verifique template/placeholders.");
                }

                return bytes;
            }

        } catch (Exception e) {
            log.error("❌ Erro ao gerar documento: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao gerar documento: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> criarMapaDadosSimplificado(AditivoContratual aditivo) {
        Map<String, Object> data = new HashMap<>();

        // Placeholders devem existir IGUAIS no DOCX: {{unidadeNome}}, etc.
        data.put("unidadeNome", nv(aditivo.getUnidadeNome()));
        data.put("unidadeCnpj", nv(aditivo.getUnidadeCnpj()));
        data.put("unidadeEndereco", nv(aditivo.getUnidadeEndereco()));

        data.put("pessoaFisicaNome", nv(aditivo.getPessoaFisicaNome()));
        data.put("pessoaFisicaCpf", nv(aditivo.getPessoaFisicaCpf()));
        data.put("pessoaFisicaEndereco", nv(aditivo.getPessoaFisicaEndereco()));

        data.put("pessoaJuridicaNome", nv(aditivo.getPessoaJuridicaNome()));
        data.put("pessoaJuridicaCnpj", nv(aditivo.getPessoaJuridicaCnpj()));
        data.put("pessoaJuridicaEndereco", nv(aditivo.getPessoaJuridicaEndereco()));

        data.put("dataInicioContrato", formatarData(aditivo.getDataInicioContrato()));
        data.put("localData", nv(aditivo.getLocalData()));
        data.put("dataAtual", java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        return data;
    }

    private String nv(String v) { return v != null ? v : ""; }

    private String formatarData(java.time.LocalDate data) {
        return data == null ? "" : data.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}