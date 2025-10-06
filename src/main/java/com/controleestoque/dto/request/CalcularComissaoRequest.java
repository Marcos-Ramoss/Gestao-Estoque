package com.controleestoque.dto.request;

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
public class CalcularComissaoRequest {
    
    @NotNull(message = "O valor da venda é obrigatório")
    @Positive(message = "O valor da venda deve ser positivo")
    private BigDecimal valorVenda;
    
    @NotNull(message = "O percentual de comissão é obrigatório")
    @Positive(message = "O percentual de comissão deve ser positivo")
    private BigDecimal percentualComissao;
}
