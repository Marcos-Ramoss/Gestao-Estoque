package com.controleestoque.repository;

import com.controleestoque.entity.Venda;
import com.controleestoque.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {
    
    boolean existsByMercadoLivreId(String mercadoLivreId);
    
    Optional<Venda> findByMercadoLivreId(String mercadoLivreId);
    
    List<Venda> findByVendedorOrderByDataDesc(Usuario vendedor);
    
    Page<Venda> findByVendedorOrderByDataDesc(Usuario vendedor, Pageable pageable);
    
    @Query("SELECT v FROM Venda v WHERE " +
           "(:produtoId IS NULL OR v.produto.id = :produtoId) AND " +
           "(:vendedorId IS NULL OR v.vendedor.id = :vendedorId) AND " +
           "(:dataInicio IS NULL OR v.data >= :dataInicio) AND " +
           "(:dataFim IS NULL OR v.data <= :dataFim)")
    Page<Venda> findByFilters(
            @Param("produtoId") Long produtoId,
            @Param("vendedorId") Long vendedorId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            Pageable pageable);
    
    @Query("SELECT SUM(v.comissao) FROM Venda v WHERE v.vendedor = :vendedor AND " +
           "(:dataInicio IS NULL OR v.data >= :dataInicio) AND " +
           "(:dataFim IS NULL OR v.data <= :dataFim)")
    Double sumComissaoByVendedorAndPeriodo(
            @Param("vendedor") Usuario vendedor,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim);
    
    @Query("SELECT SUM(v.valorVenda) FROM Venda v WHERE v.vendedor = :vendedor AND " +
           "(:dataInicio IS NULL OR v.data >= :dataInicio) AND " +
           "(:dataFim IS NULL OR v.data <= :dataFim)")
    Double sumValorVendaByVendedorAndPeriodo(
            @Param("vendedor") Usuario vendedor,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim);
}




