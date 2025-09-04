package com.formulario.athena.config;


import com.formulario.athena.dto.ContractAddendumRequest;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PlaceholderMapper {

    /**
     * Gera placeholders para o documento do ZapSign
     * @param request Dados recebidos do formulário
     * @return Map com as chaves exatamente iguais às variáveis do modelo ZapSign
     */
    public Map<String, String> gerarPlaceholders(ContractAddendumRequest request) {
        Map<String, String> placeholders = new HashMap<>();

        // Dados principais do cliente
        placeholders.put("CONTRATANTE", request.getNome());
        placeholders.put("CPF", request.getCpf());
        placeholders.put("CNPJ", request.getCnpj());
        placeholders.put("EMAIL", request.getEmail());

        // Campos específicos do aditivo contratual
        placeholders.put("ENDERECO", request.getEndereco() != null ? request.getEndereco() : "");
        placeholders.put("DATA_INICIO", request.getDataInicioContrato() != null ? request.getDataInicioContrato() : "");
        placeholders.put("CONTRATANTE_PJ", request.getNomeEmpresa() != null ? request.getNomeEmpresa() : "");

        return placeholders;
    }
}

