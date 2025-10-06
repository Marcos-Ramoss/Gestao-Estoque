package com.controleestoque.dto.mapper;

import com.controleestoque.dto.request.CreateProdutoRequest;
import com.controleestoque.dto.response.CategoriaResponse;
import com.controleestoque.dto.response.ProdutoResponse;
import com.controleestoque.entity.Categoria;
import com.controleestoque.entity.Produto;
import org.springframework.stereotype.Component;

@Component
public class ProdutoMapper {
    
    public Produto toEntity(CreateProdutoRequest request, Categoria categoria) {
        return Produto.builder()
                .nome(request.getNome())
                .descricao(request.getDescricao())
                .sku(request.getSku())
                .precoCusto(request.getPrecoCusto())
                .precoVenda(request.getPrecoVenda())
                .estoqueAtual(0)
                .estoqueMinimo(request.getEstoqueMinimo() != null ? request.getEstoqueMinimo() : 0)
                .ativo(true)
                .categoria(categoria)
                .build();
    }
    
    public ProdutoResponse toResponse(Produto produto) {
        CategoriaResponse categoriaResponse = null;
        if (produto.getCategoria() != null) {
            categoriaResponse = CategoriaResponse.builder()
                    .id(produto.getCategoria().getId())
                    .nome(produto.getCategoria().getNome())
                    .descricao(produto.getCategoria().getDescricao())
                    .ativo(produto.getCategoria().getAtivo())
                    .build();
        }
        
        return ProdutoResponse.builder()
                .id(produto.getId())
                .nome(produto.getNome())
                .descricao(produto.getDescricao())
                .sku(produto.getSku())
                .precoCusto(produto.getPrecoCusto())
                .precoVenda(produto.getPrecoVenda())
                .estoqueAtual(produto.getEstoqueAtual())
                .estoqueMinimo(produto.getEstoqueMinimo())
                .ativo(produto.getAtivo())
                .categoria(categoriaResponse)
                .createdAt(produto.getCreatedAt())
                .updatedAt(produto.getUpdatedAt())
                .build();
    }
}




