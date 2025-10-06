package com.controleestoque.dto.response;

import com.controleestoque.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    
    private String token;
    @Builder.Default
    private String tipo = "Bearer";
    private Long id;
    private String nome;
    private String email;
    private Role role;
}

