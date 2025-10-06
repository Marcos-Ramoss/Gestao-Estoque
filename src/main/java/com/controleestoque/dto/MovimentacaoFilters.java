package com.controleestoque.dto;

import com.controleestoque.enums.TipoMovimentacao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimentacaoFilters {
    
    private Long produtoId;
    private TipoMovimentacao tipo;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
}
