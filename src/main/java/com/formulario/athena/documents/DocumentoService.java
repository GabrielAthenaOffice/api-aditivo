package com.formulario.athena.documents;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.formulario.athena.model.AditivoContratual;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class DocumentoService {

    private static final String MODELO_PATH = "templates/AditivoContratual.docx";
    private static final String DESTINO_PATH = "aditivos-gerados/";

    // ✅ Retorna byte[] em vez de salvar arquivo
    public byte[] gerarAditivoContratual(AditivoContratual aditivo) {
        try {
            ClassPathResource modeloResource = new ClassPathResource(MODELO_PATH);

            if (!modeloResource.exists()) {
                throw new FileNotFoundException("Template não encontrado: " + MODELO_PATH);
            }

            Map<String, Object> data = criarMapaDadosSimplificado(aditivo);
            log.info("Gerando documento com dados: {}", data.keySet());

            Configure config = Configure.builder().build();

            // ✅ Usar InputStream diretamente
            XWPFTemplate template = XWPFTemplate.compile(modeloResource.getInputStream(), config)
                    .render(data);

            // ✅ Converter para byte[] em vez de salvar arquivo
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            template.write(outputStream);
            template.close();

            log.info("✅ Documento gerado com SUCESSO - Tamanho: {} bytes", outputStream.size());
            return outputStream.toByteArray();

        } catch (Exception e) {
            log.error("❌ Erro ao gerar documento: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao gerar documento: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> criarMapaDadosSimplificado(AditivoContratual aditivo) {
        Map<String, Object> data = new HashMap<>();

        // Unidade Athena
        data.put("unidadeNome", valorOuVazio(aditivo.getUnidadeNome()));
        data.put("unidadeCnpj", valorOuVazio(aditivo.getUnidadeCnpj()));
        data.put("unidadeEndereco", valorOuVazio(aditivo.getUnidadeEndereco()));

        // Pessoa Física
        data.put("pessoaFisicaNome", valorOuVazio(aditivo.getPessoaFisicaNome()));
        data.put("pessoaFisicaCpf", valorOuVazio(aditivo.getPessoaFisicaCpf()));
        data.put("pessoaFisicaEndereco", valorOuVazio(aditivo.getPessoaFisicaEndereco()));

        // Pessoa Jurídica
        data.put("pessoaJuridicaNome", valorOuVazio(aditivo.getPessoaJuridicaNome()));
        data.put("pessoaJuridicaCnpj", valorOuVazio(aditivo.getPessoaJuridicaCnpj()));
        data.put("pessoaJuridicaEndereco", valorOuVazio(aditivo.getPessoaJuridicaEndereco()));

        // Datas
        data.put("dataInicioContrato", formatarData(aditivo.getDataInicioContrato()));
        data.put("localData", valorOuVazio(aditivo.getLocalData()));
        data.put("dataAtual", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        return data;
    }

    private String valorOuVazio(String valor) {
        return valor != null ? valor : "";
    }

    private String gerarNomeArquivo(AditivoContratual aditivo) {
        String nomeSanitizado = "aditivo";

        if (aditivo.getPessoaJuridicaNome() != null) {
            nomeSanitizado = aditivo.getPessoaJuridicaNome()
                    .replaceAll("[^a-zA-Z0-9\\s]", "")
                    .replaceAll("\\s+", "_")
                    .toLowerCase();
        }

        // ✅ CORREÇÃO: Use o ID do aditivo em vez de timestamp
        return String.format("aditivo_%s_%s.docx", nomeSanitizado, aditivo.getId());
    }

    private String formatarData(LocalDate data) {
        if (data == null) return "";
        return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}