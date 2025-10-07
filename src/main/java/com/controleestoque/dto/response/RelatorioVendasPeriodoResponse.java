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
public class RelatorioVendasPeriodoResponse {
    
    private LocalDateTime periodoInicio;
    private LocalDateTime periodoFim;
    private Integer totalVendas;
    private Integer totalProdutosVendidos;
    private BigDecimal valorTotalVendas;
    private BigDecimal comissaoTotal;
    private Integer totalVendedores;
    private BigDecimal ticketMedio;
}
