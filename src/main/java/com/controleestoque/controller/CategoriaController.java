package com.controleestoque.controller;

import com.controleestoque.dto.request.CreateCategoriaRequest;
import com.controleestoque.dto.response.CategoriaResponse;
import com.controleestoque.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
@Validated
@Tag(name = "Categorias", description = "Operações relacionadas a categorias")
public class CategoriaController {
    
    private final CategoriaService categoriaService;
    
    @GetMapping
    @Operation(summary = "Listar categorias", description = "Retorna lista de todas as categorias ativas")
    public ResponseEntity<List<CategoriaResponse>> listarTodas() {
        List<CategoriaResponse> categorias = categoriaService.listarTodas();
        return ResponseEntity.ok(categorias);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar categoria por ID", description = "Retorna uma categoria específica pelo ID")
    public ResponseEntity<CategoriaResponse> buscarPorId(@PathVariable Long id) {
        CategoriaResponse categoria = categoriaService.buscarPorId(id);
        return ResponseEntity.ok(categoria);
    }
    
    @PostMapping
    @Operation(summary = "Criar categoria", description = "Cria uma nova categoria")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriaResponse> criar(@Valid @RequestBody CreateCategoriaRequest request) {
        CategoriaResponse categoria = categoriaService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoria);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar categoria", description = "Atualiza uma categoria existente")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriaResponse> atualizar(@PathVariable Long id, 
                                                     @Valid @RequestBody CreateCategoriaRequest request) {
        CategoriaResponse categoria = categoriaService.atualizar(id, request);
        return ResponseEntity.ok(categoria);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir categoria", description = "Exclui uma categoria")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        categoriaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}/ativar")
    @Operation(summary = "Ativar categoria", description = "Ativa uma categoria")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriaResponse> ativar(@PathVariable Long id) {
        CategoriaResponse categoria = categoriaService.ativar(id);
        return ResponseEntity.ok(categoria);
    }
    
    @PutMapping("/{id}/desativar")
    @Operation(summary = "Desativar categoria", description = "Desativa uma categoria")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriaResponse> desativar(@PathVariable Long id) {
        CategoriaResponse categoria = categoriaService.desativar(id);
        return ResponseEntity.ok(categoria);
    }
}

