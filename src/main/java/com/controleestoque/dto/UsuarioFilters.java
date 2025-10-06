package com.controleestoque.dto;

import com.controleestoque.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioFilters {
    
    private String search;
    private Role role;
    private Boolean ativo;
}




