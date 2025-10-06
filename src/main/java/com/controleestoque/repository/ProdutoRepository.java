package com.controleestoque.repository;

import com.controleestoque.entity.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    
    boolean existsBySku(String sku);
    
    Optional<Produto> findBySku(String sku);
    
    List<Produto> findByAtivoTrue();
    
    List<Produto> findByCategoriaIdAndAtivoTrue(Long categoriaId);
    
    @Query("SELECT p FROM Produto p WHERE " +
           "(:search IS NULL OR p.nome LIKE %:search% OR p.descricao LIKE %:search% OR p.sku LIKE %:search%) AND " +
           "(:categoriaId IS NULL OR p.categoria.id = :categoriaId) AND " +
           "(:ativo IS NULL OR p.ativo = :ativo)")
    Page<Produto> findByFilters(
            @Param("search") String search,
            @Param("categoriaId") Long categoriaId,
            @Param("ativo") Boolean ativo,
            Pageable pageable);
    
    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND p.estoqueAtual <= p.estoqueMinimo")
    List<Produto> findProdutosComEstoqueBaixo();
    
    @Query("SELECT p FROM Produto p WHERE p.ativo = true ORDER BY p.nome")
    List<Produto> findAllAtivosOrderByNome();
}




