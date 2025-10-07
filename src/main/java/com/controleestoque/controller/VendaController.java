package com.controleestoque.controller;

import com.controleestoque.dto.ListResponse;
import com.controleestoque.dto.PaginationDto;
import com.controleestoque.dto.VendaFilters;
import com.controleestoque.dto.request.CreateVendaRequest;
import com.controleestoque.dto.request.UpdateVendaRequest;
import com.controleestoque.dto.response.VendaResponse;
import com.controleestoque.service.VendaService;
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

@RestController
@RequestMapping("/vendas")
@RequiredArgsConstructor
@Validated
@Tag(name = "Vendas", description = "Operações relacionadas a vendas de produtos")
public class VendaController {

    private final VendaService vendaService;

    @PostMapping
    @Operation(summary = "Criar venda", description = "Cria uma nova venda e automaticamente dá baixa no estoque")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VENDEDOR')")
    public ResponseEntity<VendaResponse> criar(@Valid @RequestBody CreateVendaRequest request) {
        VendaResponse venda = vendaService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(venda);
    }

    @GetMapping
    @Operation(summary = "Listar vendas", description = "Retorna lista paginada de vendas com filtros")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VENDEDOR') or hasRole('VISUALIZADOR')")
    public ResponseEntity<ListResponse<VendaResponse>> listar(
            @Parameter(description = "ID do produto")
            @RequestParam(required = false) Long produtoId,
            
            @Parameter(description = "ID do vendedor")
            @RequestParam(required = false) Long vendedorId,
            
            @Parameter(description = "ID do Mercado Livre")
            @RequestParam(required = false) String mercadoLivreId,
            
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
        
        VendaFilters filters = VendaFilters.builder()
                .produtoId(produtoId)
                .vendedorId(vendedorId)
                .mercadoLivreId(mercadoLivreId)
                .dataInicio(dataInicio)
                .dataFim(dataFim)
                .build();

        PaginationDto pagination = PaginationDto.builder()
                .page(page)
                .limit(limit)
                .orderBy(orderBy)
                .orderDirection(orderDirection)
                .build();

        ListResponse<VendaResponse> result = vendaService.listar(filters, pagination);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar venda por ID", description = "Retorna uma venda específica pelo ID")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VENDEDOR') or hasRole('VISUALIZADOR')")
    public ResponseEntity<VendaResponse> buscarPorId(@PathVariable Long id) {
        VendaResponse venda = vendaService.buscarPorId(id);
        return ResponseEntity.ok(venda);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar venda", description = "Atualiza uma venda existente")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VENDEDOR')")
    public ResponseEntity<VendaResponse> atualizar(@PathVariable Long id, 
                                                  @Valid @RequestBody UpdateVendaRequest request) {
        VendaResponse venda = vendaService.atualizar(id, request);
        return ResponseEntity.ok(venda);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir venda", description = "Exclui uma venda")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        vendaService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/vendedor/{vendedorId}")
    @Operation(summary = "Buscar vendas por vendedor", description = "Retorna vendas de um vendedor específico")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VENDEDOR') or hasRole('VISUALIZADOR')")
    public ResponseEntity<ListResponse<VendaResponse>> buscarVendasPorVendedor(
            @PathVariable Long vendedorId,
            @Parameter(description = "Número da página")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Quantidade por página")
            @RequestParam(defaultValue = "10") int limit,
            @Parameter(description = "Campo para ordenação")
            @RequestParam(defaultValue = "data") String orderBy,
            @Parameter(description = "Direção da ordenação (asc/desc)")
            @RequestParam(defaultValue = "desc") String orderDirection) {
        
        PaginationDto pagination = PaginationDto.builder()
                .page(page)
                .limit(limit)
                .orderBy(orderBy)
                .orderDirection(orderDirection)
                .build();

        ListResponse<VendaResponse> result = vendaService.buscarVendasPorVendedor(vendedorId, pagination);
        return ResponseEntity.ok(result);
    }
}
