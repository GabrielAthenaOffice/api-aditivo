package com.formulario.athena.documents;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.formulario.athena.model.AditivoContratual;
import com.formulario.athena.model.TemplateType;
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

    private static final String TEMPLATE_DIR = "templates/";

    // NOVO: expõe o mapa de placeholders (base + extras)
    public Map<String, Object> montarPlaceholders(AditivoContratual ad, TemplateType tt, Map<String, Object> extras) {
        Map<String, Object> data = criarMapaDadosSimplificado(ad);      // base (unidade, PF, PJ, datas)
        if (extras != null) data.putAll(extras);           // DTO-only conforme rota

        // Normaliza null -> ""
        data.replaceAll((k,v) -> v == null ? "" : v);

        // Validação mínima: required do template precisam vir não-vazios
        for (String r : tt.getRequired()) {
            Object v = data.get(r);
            if (v == null || String.valueOf(v).isBlank())
                throw new IllegalArgumentException("Campo obrigatório ausente/ vazio para template "+tt.getFileBase()+": "+r);
        }
        return data;
    }


    public byte[] gerarAditivoContratual(AditivoContratual aditivo, TemplateType template, Map<String, Object> extras) {
        String path = TEMPLATE_DIR + template.getFileBase() + ".docx";
        ClassPathResource modelo = new ClassPathResource(path);
        if (!modelo.exists()) throw new IllegalStateException("Template não encontrado: " + path);

        Map<String, Object> data = montarPlaceholders(aditivo, template, extras);

        try (var in = modelo.getInputStream();
             var tpl = XWPFTemplate.compile(in, Configure.builder().build()).render(data);
             var bos = new ByteArrayOutputStream()) {
            tpl.write(bos);
            byte[] bytes = bos.toByteArray();
            if (bytes.length == 0) throw new IllegalStateException("Documento 0 bytes");
            return bytes;
        } catch (Exception e) {
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