package com.controleestoque.controller;

import com.controleestoque.dto.PaginationDto;
import com.controleestoque.dto.ListResponse;
import com.controleestoque.dto.request.CreateProdutoRequest;
import com.controleestoque.dto.response.ProdutoResponse;
import com.controleestoque.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/produtos")
@RequiredArgsConstructor
@Validated
@Tag(name = "Produtos", description = "Operações relacionadas a produtos")
public class ProdutoController {
    
    private final ProdutoService produtoService;
    
    @GetMapping
    @Operation(summary = "Listar produtos", description = "Retorna lista paginada de produtos com filtros")
    public ResponseEntity<ListResponse<ProdutoResponse>> listar(
            @Parameter(description = "Termo de busca") 
            @RequestParam(required = false) String search,
            
            @Parameter(description = "ID da categoria") 
            @RequestParam(required = false) Long categoriaId,
            
            @Parameter(description = "Status do produto") 
            @RequestParam(required = false) Boolean ativo,
            
            @Parameter(description = "Número da página") 
            @RequestParam(defaultValue = "1") int page,
            
            @Parameter(description = "Quantidade por página") 
            @RequestParam(defaultValue = "10") int limit,
            
            @Parameter(description = "Campo para ordenação") 
            @RequestParam(defaultValue = "createdAt") String orderBy,
            
            @Parameter(description = "Direção da ordenação (asc/desc)") 
            @RequestParam(defaultValue = "desc") String orderDirection) {
        
        PaginationDto pagination = PaginationDto.builder()
                .page(page)
                .limit(limit)
                .orderBy(orderBy)
                .orderDirection(orderDirection)
                .build();
                
        ListResponse<ProdutoResponse> result = produtoService.listar(search, categoriaId, ativo, pagination);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/todos")
    @Operation(summary = "Listar todos os produtos", description = "Retorna lista de todos os produtos ativos")
    public ResponseEntity<List<ProdutoResponse>> listarTodos() {
        List<ProdutoResponse> produtos = produtoService.listarTodos();
        return ResponseEntity.ok(produtos);
    }
    
    @GetMapping("/estoque-baixo")
    @Operation(summary = "Listar produtos com estoque baixo", description = "Retorna produtos com estoque atual <= estoque mínimo")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VENDEDOR')")
    public ResponseEntity<List<ProdutoResponse>> listarProdutosComEstoqueBaixo() {
        List<ProdutoResponse> produtos = produtoService.listarProdutosComEstoqueBaixo();
        return ResponseEntity.ok(produtos);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto por ID", description = "Retorna um produto específico pelo ID")
    public ResponseEntity<ProdutoResponse> buscarPorId(@PathVariable Long id) {
        ProdutoResponse produto = produtoService.buscarPorId(id);
        return ResponseEntity.ok(produto);
    }
    
    @GetMapping("/sku/{sku}")
    @Operation(summary = "Buscar produto por SKU", description = "Retorna um produto específico pelo SKU")
    public ResponseEntity<ProdutoResponse> buscarPorSku(@PathVariable String sku) {
        ProdutoResponse produto = produtoService.buscarPorSku(sku);
        return ResponseEntity.ok(produto);
    }
    
    @PostMapping
    @Operation(summary = "Criar produto", description = "Cria um novo produto")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VENDEDOR')")
    public ResponseEntity<ProdutoResponse> criar(@Valid @RequestBody CreateProdutoRequest request) {
        ProdutoResponse produto = produtoService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(produto);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar produto", description = "Atualiza um produto existente")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProdutoResponse> atualizar(@PathVariable Long id, 
                                                   @Valid @RequestBody CreateProdutoRequest request) {
        ProdutoResponse produto = produtoService.atualizar(id, request);
        return ResponseEntity.ok(produto);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir produto", description = "Exclui um produto")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        produtoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}/ativar")
    @Operation(summary = "Ativar produto", description = "Ativa um produto")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProdutoResponse> ativar(@PathVariable Long id) {
        ProdutoResponse produto = produtoService.ativar(id);
        return ResponseEntity.ok(produto);
    }
    
    @PutMapping("/{id}/desativar")
    @Operation(summary = "Desativar produto", description = "Desativa um produto")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProdutoResponse> desativar(@PathVariable Long id) {
        ProdutoResponse produto = produtoService.desativar(id);
        return ResponseEntity.ok(produto);
    }
}

