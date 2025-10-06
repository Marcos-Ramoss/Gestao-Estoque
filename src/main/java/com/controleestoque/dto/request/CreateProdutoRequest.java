package com.controleestoque.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProdutoRequest {
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
    private String nome;
    
    private String descricao;
    
    @NotBlank(message = "SKU é obrigatório")
    @Size(max = 100, message = "SKU deve ter no máximo 100 caracteres")
    private String sku;
    
    @NotNull(message = "Preço de custo é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Preço de custo deve ser maior que zero")
    private BigDecimal precoCusto;
    
    @NotNull(message = "Preço de venda é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Preço de venda deve ser maior que zero")
    private BigDecimal precoVenda;
    
    @Min(value = 0, message = "Estoque mínimo não pode ser negativo")
    private Integer estoqueMinimo;
    
    @NotNull(message = "Categoria é obrigatória")
    private Long categoriaId;
}




