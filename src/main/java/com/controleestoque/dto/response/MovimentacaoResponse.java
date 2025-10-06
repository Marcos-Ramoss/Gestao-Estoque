package com.controleestoque.dto.response;

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
public class MovimentacaoResponse {
    
    private Long id;
    private Long produtoId;
    private String produtoNome;
    private String produtoSku;
    private TipoMovimentacao tipo;
    private Integer quantidade;
    private String motivo;
    private LocalDateTime data;
    private LocalDateTime createdAt;
}
