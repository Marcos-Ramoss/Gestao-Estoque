package com.controleestoque.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProdutoResponse {
    
    private Long id;
    private String nome;
    private String descricao;
    private String sku;
    private BigDecimal precoCusto;
    private BigDecimal precoVenda;
    private Integer estoqueAtual;
    private Integer estoqueMinimo;
    private Boolean ativo;
    private CategoriaResponse categoria;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}




