package com.controleestoque.repository;

import com.controleestoque.entity.Usuario;
import com.controleestoque.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    boolean existsByEmail(String email);
    
    Optional<Usuario> findByEmail(String email);
    
    List<Usuario> findByRoleAndAtivoTrue(Role role);
    
    @Query("SELECT u FROM Usuario u WHERE " +
           "(:search IS NULL OR u.nome LIKE %:search% OR u.email LIKE %:search%) AND " +
           "(:role IS NULL OR u.role = :role) AND " +
           "(:ativo IS NULL OR u.ativo = :ativo)")
    Page<Usuario> findByFilters(
            @Param("search") String search,
            @Param("role") Role role,
            @Param("ativo") Boolean ativo,
            Pageable pageable);
    
    @Query("SELECT u FROM Usuario u WHERE u.ativo = true AND u.role = :role")
    List<Usuario> findVendedoresAtivos(@Param("role") Role role);
}




