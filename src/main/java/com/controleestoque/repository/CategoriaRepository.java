package com.controleestoque.repository;

import com.controleestoque.entity.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    
    boolean existsByNome(String nome);
    
    Optional<Categoria> findByNome(String nome);
    
    List<Categoria> findByAtivoTrue();
    
    @Query("SELECT c FROM Categoria c WHERE " +
           "(:search IS NULL OR c.nome LIKE %:search% OR c.descricao LIKE %:search%) AND " +
           "(:ativo IS NULL OR c.ativo = :ativo)")
    Page<Categoria> findByFilters(
            @Param("search") String search,
            @Param("ativo") Boolean ativo,
            Pageable pageable);
    
    @Query("SELECT c FROM Categoria c WHERE c.ativo = true ORDER BY c.nome")
    List<Categoria> findAllAtivasOrderByNome();
}




