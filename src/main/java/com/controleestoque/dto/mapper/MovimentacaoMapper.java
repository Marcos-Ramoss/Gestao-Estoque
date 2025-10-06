package com.controleestoque.dto.mapper;

import com.controleestoque.dto.request.CreateMovimentacaoRequest;
import com.controleestoque.dto.response.MovimentacaoResponse;
import com.controleestoque.entity.MovimentacaoEstoque;
import org.springframework.stereotype.Component;

@Component
public class MovimentacaoMapper {
    
    public MovimentacaoEstoque toEntity(CreateMovimentacaoRequest request) {
        if (request == null) {
            return null;
        }
        
        return MovimentacaoEstoque.builder()
                .tipo(request.getTipo())
                .quantidade(request.getQuantidade())
                .motivo(request.getMotivo())
                .build();
    }
    
    public MovimentacaoResponse toResponse(MovimentacaoEstoque movimentacao) {
        if (movimentacao == null) {
            return null;
        }
        
        return MovimentacaoResponse.builder()
                .id(movimentacao.getId())
                .produtoId(movimentacao.getProduto().getId())
                .produtoNome(movimentacao.getProduto().getNome())
                .produtoSku(movimentacao.getProduto().getSku())
                .tipo(movimentacao.getTipo())
                .quantidade(movimentacao.getQuantidade())
                .motivo(movimentacao.getMotivo())
                .data(movimentacao.getData())
                .createdAt(movimentacao.getCreatedAt())
                .build();
    }
}
