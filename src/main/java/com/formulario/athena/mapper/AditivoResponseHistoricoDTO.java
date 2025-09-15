package com.formulario.athena.mapper;

import com.formulario.athena.dto.AditivoSimpleResponseDTO;
import com.formulario.athena.dto.HistoricoResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AditivoResponseHistoricoDTO {
    private List<HistoricoResponseDTO> content;
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalElements;
    private Integer totalPages;
    private boolean lastPage;
}
