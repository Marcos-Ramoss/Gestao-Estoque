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
public class ComissaoResponse {
    
    private Long id;
    private Long vendedorId;
    private String nomeVendedor;
    private String emailVendedor;
    private Long produtoId;
    private String nomeProduto;
    private String skuProduto;
    private Integer quantidade;
    private BigDecimal valorVenda;
    private BigDecimal comissao;
    private BigDecimal percentualComissao;
    private String mercadoLivreId;
    private LocalDateTime dataVenda;
    private LocalDateTime createdAt;
}
