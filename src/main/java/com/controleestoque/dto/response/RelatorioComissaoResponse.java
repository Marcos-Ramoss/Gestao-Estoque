package com.controleestoque.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RelatorioComissaoResponse {
    
    private Long vendedorId;
    private String nomeVendedor;
    private String emailVendedor;
    private LocalDateTime periodoInicio;
    private LocalDateTime periodoFim;
    private Integer totalVendas;
    private BigDecimal valorTotalVendas;
    private BigDecimal comissaoTotal;
    private BigDecimal percentualComissao;
    private List<ComissaoResponse> detalhesVendas;
}
