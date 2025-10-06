package com.controleestoque.dto.mapper;

import com.controleestoque.dto.response.ComissaoResponse;
import com.controleestoque.entity.Venda;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class ComissaoMapper {

    public ComissaoResponse toResponse(Venda venda) {
        BigDecimal percentualComissao = BigDecimal.ZERO;
        
        if (venda.getValorVenda() != null && venda.getValorVenda().compareTo(BigDecimal.ZERO) > 0) {
            percentualComissao = venda.getComissao()
                .multiply(BigDecimal.valueOf(100))
                .divide(venda.getValorVenda(), 2, RoundingMode.HALF_UP);
        }
        
        return ComissaoResponse.builder()
                .id(venda.getId())
                .vendedorId(venda.getVendedor().getId())
                .nomeVendedor(venda.getVendedor().getNome())
                .emailVendedor(venda.getVendedor().getEmail())
                .produtoId(venda.getProduto().getId())
                .nomeProduto(venda.getProduto().getNome())
                .skuProduto(venda.getProduto().getSku())
                .quantidade(venda.getQuantidade())
                .valorVenda(venda.getValorVenda())
                .comissao(venda.getComissao())
                .percentualComissao(percentualComissao)
                .mercadoLivreId(venda.getMercadoLivreId())
                .dataVenda(venda.getData())
                .createdAt(venda.getCreatedAt())
                .build();
    }
}
