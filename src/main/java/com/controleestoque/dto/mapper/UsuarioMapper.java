package com.controleestoque.dto.mapper;

import com.controleestoque.dto.request.CreateUsuarioRequest;
import com.controleestoque.dto.request.UpdateUsuarioRequest;
import com.controleestoque.dto.response.UsuarioResponse;
import com.controleestoque.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {
    
    public Usuario toEntity(CreateUsuarioRequest request) {
        return Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(request.getSenha()) // Será criptografada no service
                .role(request.getRole())
                .ativo(true)
                .build();
    }
    
    public Usuario updateEntity(Usuario usuario, UpdateUsuarioRequest request) {
        if (request.getNome() != null) {
            usuario.setNome(request.getNome());
        }
        if (request.getEmail() != null) {
            usuario.setEmail(request.getEmail());
        }
        if (request.getRole() != null) {
            usuario.setRole(request.getRole());
        }
        if (request.getAtivo() != null) {
            usuario.setAtivo(request.getAtivo());
        }
        return usuario;
    }
    
    public UsuarioResponse toResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .role(usuario.getRole())
                .ativo(usuario.getAtivo())
                .createdAt(usuario.getCreatedAt())
                .updatedAt(usuario.getUpdatedAt())
                .build();
    }
}




