package com.controleestoque.dto.mapper;

import com.controleestoque.dto.request.CreateCategoriaRequest;
import com.controleestoque.dto.response.CategoriaResponse;
import com.controleestoque.entity.Categoria;
import org.springframework.stereotype.Component;

@Component
public class CategoriaMapper {
    
    public Categoria toEntity(CreateCategoriaRequest request) {
        return Categoria.builder()
                .nome(request.getNome())
                .descricao(request.getDescricao())
                .ativo(true)
                .build();
    }
    
    public CategoriaResponse toResponse(Categoria categoria) {
        return CategoriaResponse.builder()
                .id(categoria.getId())
                .nome(categoria.getNome())
                .descricao(categoria.getDescricao())
                .ativo(categoria.getAtivo())
                .createdAt(categoria.getCreatedAt())
                .updatedAt(categoria.getUpdatedAt())
                .build();
    }
}




