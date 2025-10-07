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
public class RelatorioResumoEstoqueResponse {
    
    private Integer totalProdutos;
    private Integer produtosComEstoque;
    private Integer produtosSemEstoque;
    private Integer produtosEstoqueBaixo;
    private Integer produtosEstoqueCritico;
    private BigDecimal valorTotalEstoque;
    private BigDecimal valorMedioProduto;
    private Integer totalMovimentacoes;
}
