package com.controleestoque.controller;

import com.controleestoque.dto.ListResponse;
import com.controleestoque.dto.MovimentacaoFilters;
import com.controleestoque.dto.PaginationDto;
import com.controleestoque.dto.request.CreateMovimentacaoRequest;
import com.controleestoque.dto.request.EntradaEstoqueRequest;
import com.controleestoque.dto.request.SaidaEstoqueRequest;
import com.controleestoque.dto.response.MovimentacaoResponse;
import com.controleestoque.enums.TipoMovimentacao;
import com.controleestoque.service.MovimentacaoEstoqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/movimentacoes")
@RequiredArgsConstructor
@Validated
@Tag(name = "Movimentações de Estoque", description = "Operações relacionadas a movimentações de estoque")
public class MovimentacaoEstoqueController {
    
    private final MovimentacaoEstoqueService movimentacaoService;
    
    @GetMapping
    @Operation(summary = "Listar movimentações", description = "Retorna lista paginada de movimentações com filtros")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VENDEDOR')")
    public ResponseEntity<ListResponse<MovimentacaoResponse>> listar(
            @Parameter(description = "ID do produto") 
            @RequestParam(required = false) Long produtoId,
            
            @Parameter(description = "Tipo de movimentação") 
            @RequestParam(required = false) TipoMovimentacao tipo,
            
            @Parameter(description = "Data início (formato: yyyy-MM-dd'T'HH:mm:ss)") 
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            
            @Parameter(description = "Data fim (formato: yyyy-MM-dd'T'HH:mm:ss)") 
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,
            
            @Parameter(description = "Número da página") 
            @RequestParam(defaultValue = "1") int page,
            
            @Parameter(description = "Quantidade por página") 
            @RequestParam(defaultValue = "10") int limit,
            
            @Parameter(description = "Campo para ordenação") 
            @RequestParam(defaultValue = "data") String orderBy,
            
            @Parameter(description = "Direção da ordenação (asc/desc)") 
            @RequestParam(defaultValue = "desc") String orderDirection) {
        
        MovimentacaoFilters filters = MovimentacaoFilters.builder()
                .produtoId(produtoId)
                .tipo(tipo)
                .dataInicio(dataInicio)
                .dataFim(dataFim)
                .build();
                
        PaginationDto pagination = PaginationDto.builder()
                .page(page)
                .limit(limit)
                .orderBy(orderBy)
                .orderDirection(orderDirection)
                .build();
                
        ListResponse<MovimentacaoResponse> result = movimentacaoService.listar(filters, pagination);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar movimentação por ID", description = "Retorna uma movimentação específica pelo ID")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VENDEDOR')")
    public ResponseEntity<MovimentacaoResponse> buscarPorId(@PathVariable Long id) {
        MovimentacaoResponse movimentacao = movimentacaoService.buscarPorId(id);
        return ResponseEntity.ok(movimentacao);
    }
    
    @GetMapping("/produto/{produtoId}")
    @Operation(summary = "Listar movimentações por produto", description = "Retorna histórico de movimentações de um produto")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VENDEDOR')")
    public ResponseEntity<List<MovimentacaoResponse>> listarPorProduto(@PathVariable Long produtoId) {
        List<MovimentacaoResponse> movimentacoes = movimentacaoService.listarPorProduto(produtoId);
        return ResponseEntity.ok(movimentacoes);
    }
    
    @PostMapping("/entrada")
    @Operation(summary = "Registrar entrada de estoque", description = "Registra uma entrada de estoque e atualiza o saldo do produto")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VENDEDOR')")
    public ResponseEntity<MovimentacaoResponse> registrarEntrada(@Valid @RequestBody EntradaEstoqueRequest request) {
        MovimentacaoResponse movimentacao = movimentacaoService.registrarEntrada(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(movimentacao);
    }
    
    @PostMapping("/saida")
    @Operation(summary = "Registrar saída de estoque", description = "Registra uma saída de estoque e atualiza o saldo do produto")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VENDEDOR')")
    public ResponseEntity<MovimentacaoResponse> registrarSaida(@Valid @RequestBody SaidaEstoqueRequest request) {
        MovimentacaoResponse movimentacao = movimentacaoService.registrarSaida(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(movimentacao);
    }
    
    @PostMapping
    @Operation(summary = "Criar movimentação", description = "Cria uma movimentação genérica (entrada, saída ou ajuste)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovimentacaoResponse> criar(@Valid @RequestBody CreateMovimentacaoRequest request) {
        MovimentacaoResponse movimentacao = movimentacaoService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(movimentacao);
    }
}
