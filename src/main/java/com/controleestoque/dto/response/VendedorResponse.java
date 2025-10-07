package com.controleestoque.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendedorResponse {
    
    private Long id;
    private String nome;
    private String email;
    private String role;
    private Boolean ativo;
}
