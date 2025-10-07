package com.controleestoque.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateVendaRequest {
    
    @NotNull(message = "O ID do produto é obrigatório")
    private Long produtoId;
    
    @NotNull(message = "O ID do vendedor é obrigatório")
    private Long vendedorId;
    
    @NotNull(message = "A quantidade é obrigatória")
    @Min(value = 1, message = "A quantidade deve ser no mínimo 1")
    private Integer quantidade;
    
    @NotNull(message = "O valor da venda é obrigatório")
    @Positive(message = "O valor da venda deve ser positivo")
    private BigDecimal valorVenda;
    
    @Positive(message = "O percentual de comissão deve ser positivo")
    private BigDecimal percentualComissao;
    
    private String mercadoLivreId;
}
