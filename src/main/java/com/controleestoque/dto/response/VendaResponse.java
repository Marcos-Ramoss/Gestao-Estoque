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
public class VendaResponse {
    
    private Long id;
    private Long produtoId;
    private String nomeProduto;
    private String skuProduto;
    private String categoriaProduto;
    private Long vendedorId;
    private String nomeVendedor;
    private String emailVendedor;
    private Integer quantidade;
    private BigDecimal valorVenda;
    private BigDecimal comissao;
    private BigDecimal percentualComissao;
    private String mercadoLivreId;
    private LocalDateTime dataVenda;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
