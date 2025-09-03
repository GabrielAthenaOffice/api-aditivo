package com.formulario.athena.config;


import com.formulario.athena.dto.ContractAddendumRequest;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PlaceholderMapper {

    public Map<String, String> gerarPlaceholders(ContractAddendumRequest request, Map<String, Object> dadosCorrespondencia) {
        Map<String, String> placeholders = new HashMap<>();

        // Dados do formulário
        placeholders.put("contratante", request.getNome());
        placeholders.put("cpf", request.getCpf());
        placeholders.put("cnpj", request.getCnpj());
        placeholders.put("email", request.getEmail());

        // Dados vindos da API de correspondências
        if (dadosCorrespondencia != null) {
            placeholders.put("endereco_completo", (String) dadosCorrespondencia.getOrDefault("endereco", "ENDEREÇO NÃO INFORMADO"));
            placeholders.put("contratante_pessoa_juridica", (String) dadosCorrespondencia.getOrDefault("nome_empresa", request.getNome()));
            placeholders.put("data_inicio_contrato", (String) dadosCorrespondencia.getOrDefault("data_inicio_contrato", "01/09/2025"));

        }

        // Campos adicionais
        placeholders.put("dados_pessoais", "CNPJ: " + request.getCnpj() + " | Email: " + request.getEmail());
        placeholders.put("novo_contratante_razao_social", request.getNome());
        placeholders.put("contratante_pessoa_fisica", request.getNome());

        return placeholders;
    }
}
