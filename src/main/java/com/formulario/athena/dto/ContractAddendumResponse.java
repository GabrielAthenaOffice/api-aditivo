package com.formulario.athena.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractAddendumResponse {
    private String id;
    private String zapsignLink;
    private String status;
}
