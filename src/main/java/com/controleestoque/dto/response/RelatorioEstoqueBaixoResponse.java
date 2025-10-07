package com.controleestoque.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RelatorioEstoqueBaixoResponse {
    
    private Long id;
    private String nome;
    private String sku;
    private String categoria;
    private Integer estoqueAtual;
    private Integer estoqueMinimo;
    private BigDecimal precoVenda;
    private Integer diferenca; // quanto falta para atingir o estoque mínimo
    private String status; // "CRÍTICO", "BAIXO", "ADEQUADO"
}
