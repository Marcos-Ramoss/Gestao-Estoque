package com.controleestoque.repository;

import com.controleestoque.entity.MovimentacaoEstoque;
import com.controleestoque.entity.Produto;
import com.controleestoque.enums.TipoMovimentacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {
    
    List<MovimentacaoEstoque> findByProdutoOrderByDataDesc(Produto produto);
    
    List<MovimentacaoEstoque> findByProdutoAndTipoOrderByDataDesc(Produto produto, TipoMovimentacao tipo);
    
    @Query("SELECT m FROM MovimentacaoEstoque m WHERE " +
           "(:produtoId IS NULL OR m.produto.id = :produtoId) AND " +
           "(:tipo IS NULL OR m.tipo = :tipo) AND " +
           "(:dataInicio IS NULL OR m.data >= :dataInicio) AND " +
           "(:dataFim IS NULL OR m.data <= :dataFim)")
    Page<MovimentacaoEstoque> findByFilters(
            @Param("produtoId") Long produtoId,
            @Param("tipo") TipoMovimentacao tipo,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            Pageable pageable);
    
    @Query("SELECT SUM(m.quantidade) FROM MovimentacaoEstoque m WHERE m.produto = :produto AND m.tipo = :tipo")
    Integer sumQuantidadeByProdutoAndTipo(@Param("produto") Produto produto, @Param("tipo") TipoMovimentacao tipo);
}




