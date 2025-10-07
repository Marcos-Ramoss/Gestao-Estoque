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
public class ProdutoMaisVendidoResponse {
    
    private Long produtoId;
    private String nomeProduto;
    private String skuProduto;
    private String categoriaProduto;
    private Integer quantidadeVendida;
    private BigDecimal valorTotalVendas;
    private BigDecimal valorMedioVenda;
    private Integer numeroVendas;
    private Integer estoqueAtual;
}
