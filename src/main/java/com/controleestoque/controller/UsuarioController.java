package com.controleestoque.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.controleestoque.dto.ListResponse;
import com.controleestoque.dto.PaginationDto;
import com.controleestoque.dto.UsuarioFilters;
import com.controleestoque.dto.request.CreateUsuarioRequest;
import com.controleestoque.dto.request.UpdateUsuarioRequest;
import com.controleestoque.dto.response.UsuarioResponse;
import com.controleestoque.enums.Role;
import com.controleestoque.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Validated
@Tag(name = "Usuários", description = "Operações relacionadas a usuários")
public class UsuarioController {
    
    private final UsuarioService usuarioService;
    
    @GetMapping
    @Operation(summary = "Listar usuários", description = "Retorna lista paginada de usuários com filtros")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ListResponse<UsuarioResponse>> listar(
            @Parameter(description = "Termo de busca") 
            @RequestParam(required = false) String search,
            
            @Parameter(description = "Papel do usuário") 
            @RequestParam(required = false) Role role,
            
            @Parameter(description = "Status do usuário") 
            @RequestParam(required = false) Boolean ativo,
            
            @Parameter(description = "Número da página") 
            @RequestParam(defaultValue = "1") int page,
            
            @Parameter(description = "Quantidade por página") 
            @RequestParam(defaultValue = "10") int limit,
            
            @Parameter(description = "Campo para ordenação") 
            @RequestParam(defaultValue = "createdAt") String orderBy,
            
            @Parameter(description = "Direção da ordenação (asc/desc)") 
            @RequestParam(defaultValue = "desc") String orderDirection) {
        
        UsuarioFilters filters = UsuarioFilters.builder()
                .search(search)
                .role(role)
                .ativo(ativo)
                .build();
                
        PaginationDto pagination = PaginationDto.builder()
                .page(page)
                .limit(limit)
                .orderBy(orderBy)
                .orderDirection(orderDirection)
                .build();
                
        ListResponse<UsuarioResponse> result = usuarioService.listar(filters, pagination);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "Retorna um usuário específico pelo ID")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable Long id) {
        UsuarioResponse usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(usuario);
    }
    
    @PostMapping
    @Operation(summary = "Criar usuário", description = "Cria um novo usuário")
    public ResponseEntity<UsuarioResponse> criar(@Valid @RequestBody CreateUsuarioRequest request) {
        UsuarioResponse usuario = usuarioService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário", description = "Atualiza um usuário existente")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> atualizar(@PathVariable Long id, 
                                                   @Valid @RequestBody UpdateUsuarioRequest request) {
        UsuarioResponse usuario = usuarioService.atualizar(id, request);
        return ResponseEntity.ok(usuario);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir usuário", description = "Exclui um usuário")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        usuarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}/ativar")
    @Operation(summary = "Ativar usuário", description = "Ativa um usuário")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> ativar(@PathVariable Long id) {
        UsuarioResponse usuario = usuarioService.ativar(id);
        return ResponseEntity.ok(usuario);
    }
    
    @PutMapping("/{id}/desativar")
    @Operation(summary = "Desativar usuário", description = "Desativa um usuário")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> desativar(@PathVariable Long id) {
        UsuarioResponse usuario = usuarioService.desativar(id);
        return ResponseEntity.ok(usuario);
    }
}
