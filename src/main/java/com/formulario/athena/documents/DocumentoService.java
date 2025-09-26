package com.formulario.athena.documents;

import com.formulario.athena.model.AditivoContratual;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DocumentoService {

    private static final String MODELO_PATH = "templates/AditivoContratual.docx";
    private static final String DESTINO_PATH = "aditivos-gerados/";

    public String gerarAditivoContratual(AditivoContratual aditivo) {
        try {
            // Carrega o modelo
            ClassPathResource modeloResource = new ClassPathResource(MODELO_PATH);
            XWPFDocument documento = new XWPFDocument(modeloResource.getInputStream());

            // Mapa de placeholders e valores
            Map<String, String> placeholders = criarMapaPlaceholders(aditivo);
            log.info("Placeholders a substituir: {}", placeholders);

            // Substitui placeholders no documento
            substituirPlaceholders(documento, placeholders);

            // Salva o novo documento
            String nomeArquivo = gerarNomeArquivo(aditivo);
            String caminhoCompleto = DESTINO_PATH + nomeArquivo;

            File destinoDir = new File(DESTINO_PATH);
            if (!destinoDir.exists()) {
                destinoDir.mkdirs();
            }

            try (FileOutputStream out = new FileOutputStream(caminhoCompleto)) {
                documento.write(out);
            }

            documento.close();

            log.info("Documento gerado com sucesso: {}", caminhoCompleto);
            return caminhoCompleto;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar documento: " + e.getMessage(), e);
        }
    }

    private Map<String, String> criarMapaPlaceholders(AditivoContratual aditivo) {
        Map<String, String> placeholders = new HashMap<>();

        // Dados da Unidade
        placeholders.put("{{ATHENA OFFICE LTDA}}", aditivo.getUnidadeNome() != null ? aditivo.getUnidadeNome() : "");
        placeholders.put("{{CNPJ UNIDADE}}", aditivo.getUnidadeCnpj() != null ? aditivo.getUnidadeCnpj() : "");
        placeholders.put("{{CIDADE ESTADO RUA Nº BAIRRO DA UNIDADE}}", aditivo.getUnidadeEndereco() != null ? aditivo.getUnidadeEndereco() : "");

        // Dados da Pessoa Física
        placeholders.put("{{PESSOA FÍSICA}}", aditivo.getPessoaFisicaNome() != null ? aditivo.getPessoaFisicaNome() : "");
        placeholders.put("{{CPF PESSOA FÍSICA}}", aditivo.getPessoaFisicaCpf() != null ? aditivo.getPessoaFisicaCpf() : "");
        placeholders.put("{{ENDEREÇO COMPLETO PF}}", aditivo.getPessoaFisicaEndereco() != null ? aditivo.getPessoaFisicaEndereco() : "");

        // Dados da Pessoa Jurídica
        placeholders.put("{{PESSOA JURÍDICA}}", aditivo.getPessoaJuridicaNome() != null ? aditivo.getPessoaJuridicaNome() : "");
        placeholders.put("{{CNPJ CONTRATANTE}}", aditivo.getPessoaJuridicaCnpj() != null ? aditivo.getPessoaJuridicaCnpj() : "");

        // Datas
        placeholders.put("{{DATA DE INÍCIO DO CONTRATO}}", formatarData(aditivo.getDataInicioContrato()));
        placeholders.put("{{LOCAL E DATA}}", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        return placeholders;
    }

    private void substituirPlaceholders(XWPFDocument documento, Map<String, String> placeholders) {
        // Substitui em parágrafos
        for (XWPFParagraph paragraph : documento.getParagraphs()) {
            substituirPlaceholdersNoParagrafo(paragraph, placeholders);
        }

        // Substitui em tabelas
        for (XWPFTable table : documento.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        substituirPlaceholdersNoParagrafo(paragraph, placeholders);
                    }
                }
            }
        }

        // Substitui em headers e footers
        substituirPlaceholdersHeadersFooters(documento, placeholders);
    }

    private void substituirPlaceholdersNoParagrafo(XWPFParagraph paragraph, Map<String, String> placeholders) {
        String textoOriginal = paragraph.getText();
        if (textoOriginal == null || textoOriginal.isEmpty()) {
            return;
        }

        String textoSubstituido = textoOriginal;

        // Substitui TODOS os placeholders no texto
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            if (textoSubstituido.contains(entry.getKey())) {
                textoSubstituido = textoSubstituido.replace(entry.getKey(), entry.getValue());
            }
        }

        // Se houve alteração, aplica a substituição
        if (!textoSubstituido.equals(textoOriginal)) {
            // Remove todos os runs existentes
            for (int i = paragraph.getRuns().size() - 1; i >= 0; i--) {
                paragraph.removeRun(i);
            }

            // Cria um novo run com o texto substituído
            XWPFRun newRun = paragraph.createRun();
            newRun.setText(textoSubstituido);

            // Mantém a formatação básica (opcional)
            if (!paragraph.getRuns().isEmpty()) {
                XWPFRun originalRun = paragraph.getRuns().get(0);
                newRun.setFontFamily(originalRun.getFontFamily());
                newRun.setFontSize(originalRun.getFontSize());
                newRun.setBold(originalRun.isBold());
                newRun.setItalic(originalRun.isItalic());
            }
        }
    }

    private void substituirPlaceholdersHeadersFooters(XWPFDocument documento, Map<String, String> placeholders) {
        // Headers
        for (XWPFHeader header : documento.getHeaderList()) {
            for (XWPFParagraph paragraph : header.getParagraphs()) {
                substituirPlaceholdersNoParagrafo(paragraph, placeholders);
            }
        }

        // Footers
        for (XWPFFooter footer : documento.getFooterList()) {
            for (XWPFParagraph paragraph : footer.getParagraphs()) {
                substituirPlaceholdersNoParagrafo(paragraph, placeholders);
            }
        }
    }

    // Método ALTERNATIVO mais robusto para substituição
    private void substituirPlaceholdersRobusto(XWPFDocument documento, Map<String, String> placeholders) {
        try {
            // Converte para texto, substitui e reconstrói
            String textoCompleto = extrairTexto(documento);

            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                textoCompleto = textoCompleto.replace(entry.getKey(), entry.getValue());
            }

            // Limpa e reconstrói o documento (mais radical, mas funciona)
            reconstruirDocumento(documento, textoCompleto);

        } catch (Exception e) {
            log.warn("Método robusto falhou, usando método padrão");
        }
    }

    private String extrairTexto(XWPFDocument documento) {
        StringBuilder texto = new StringBuilder();

        for (XWPFParagraph paragraph : documento.getParagraphs()) {
            texto.append(paragraph.getText()).append("\n");
        }

        return texto.toString();
    }

    private void reconstruirDocumento(XWPFDocument documento, String textoCompleto) {
        // Remove todos os parágrafos existentes
        List<XWPFParagraph> paragraphs = new ArrayList<>(documento.getParagraphs());
        for (XWPFParagraph paragraph : paragraphs) {
            documento.removeBodyElement(documento.getPosOfParagraph(paragraph));
        }

        // Adiciona novo parágrafo com todo o texto
        XWPFParagraph newParagraph = documento.createParagraph();
        XWPFRun run = newParagraph.createRun();
        run.setText(textoCompleto);
    }

    private String gerarNomeArquivo(AditivoContratual aditivo) {
        String nomeSanitizado = aditivo.getPessoaJuridicaNome()
                .replaceAll("[^a-zA-Z0-9]", "_")
                .toLowerCase();

        return String.format("aditivo_%s_%s.docx", nomeSanitizado, aditivo.getId());
    }

    private String formatarData(LocalDate data) {
        if (data == null) return "";
        return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}