package com.controleestoque.controller;

import com.controleestoque.dto.ComissaoFilters;
import com.controleestoque.dto.ListResponse;
import com.controleestoque.dto.PaginationDto;
import com.controleestoque.dto.request.CalcularComissaoRequest;
import com.controleestoque.dto.response.ComissaoResponse;
import com.controleestoque.dto.response.RelatorioComissaoResponse;
import com.controleestoque.entity.Usuario;
import com.controleestoque.service.ComissaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/comissoes")
@RequiredArgsConstructor
@Validated
@Tag(name = "Comissões", description = "Operações relacionadas a comissões de vendas")
public class ComissaoController {

    private final ComissaoService comissaoService;

    @PostMapping("/calcular")
    @Operation(summary = "Calcular comissão", description = "Calcula o valor da comissão baseado no valor da venda e percentual")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VENDEDOR')")
    public ResponseEntity<BigDecimal> calcularComissao(@Valid @RequestBody CalcularComissaoRequest request) {
        BigDecimal comissao = comissaoService.calcularComissao(request);
        return ResponseEntity.ok(comissao);
    }

    @GetMapping("/calcular/{vendedorId}")
    @Operation(summary = "Calcular comissão total do vendedor", description = "Calcula o total de comissões de um vendedor em um período")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VENDEDOR')")
    public ResponseEntity<BigDecimal> calcularComissaoTotal(
            @PathVariable Long vendedorId,
            @Parameter(description = "Data de início (formato: yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @Parameter(description = "Data de fim (formato: yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
        
        RelatorioComissaoResponse relatorio = comissaoService.gerarRelatorioComissoesPorVendedor(
                vendedorId, dataInicio, dataFim);
        return ResponseEntity.ok(relatorio.getComissaoTotal());
    }

    @GetMapping
    @Operation(summary = "Listar comissões", description = "Retorna lista paginada de comissões com filtros")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VENDEDOR') or hasRole('VISUALIZADOR')")
    public ResponseEntity<ListResponse<ComissaoResponse>> listar(
            @Parameter(description = "ID do vendedor")
            @RequestParam(required = false) Long vendedorId,
            
            @Parameter(description = "ID do produto")
            @RequestParam(required = false) Long produtoId,
            
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
            @RequestParam(defaultValue = "dataVenda") String orderBy,
            
            @Parameter(description = "Direção da ordenação (asc/desc)")
            @RequestParam(defaultValue = "desc") String orderDirection) {
        
        ComissaoFilters filters = ComissaoFilters.builder()
                .vendedorId(vendedorId)
                .produtoId(produtoId)
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

        ListResponse<ComissaoResponse> result = comissaoService.listarComissoes(filters, pagination);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/vendedor/{vendedorId}")
    @Operation(summary = "Buscar comissões por vendedor", description = "Retorna comissões de um vendedor específico")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VENDEDOR') or hasRole('VISUALIZADOR')")
    public ResponseEntity<ListResponse<ComissaoResponse>> buscarComissoesPorVendedor(
            @PathVariable Long vendedorId,
            @Parameter(description = "Número da página")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Quantidade por página")
            @RequestParam(defaultValue = "10") int limit,
            @Parameter(description = "Campo para ordenação")
            @RequestParam(defaultValue = "dataVenda") String orderBy,
            @Parameter(description = "Direção da ordenação (asc/desc)")
            @RequestParam(defaultValue = "desc") String orderDirection) {
        
        PaginationDto pagination = PaginationDto.builder()
                .page(page)
                .limit(limit)
                .orderBy(orderBy)
                .orderDirection(orderDirection)
                .build();

        ListResponse<ComissaoResponse> result = comissaoService.buscarComissoesPorVendedor(vendedorId, pagination);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/relatorio/vendedor/{vendedorId}")
    @Operation(summary = "Relatório de comissões por vendedor", description = "Gera relatório detalhado de comissões de um vendedor em um período")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VENDEDOR')")
    public ResponseEntity<RelatorioComissaoResponse> gerarRelatorioComissoesPorVendedor(
            @PathVariable Long vendedorId,
            @Parameter(description = "Data de início (formato: yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @Parameter(description = "Data de fim (formato: yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
        
        RelatorioComissaoResponse relatorio = comissaoService.gerarRelatorioComissoesPorVendedor(
                vendedorId, dataInicio, dataFim);
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar comissão por ID", description = "Retorna uma comissão específica pelo ID")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VENDEDOR') or hasRole('VISUALIZADOR')")
    public ResponseEntity<ComissaoResponse> buscarPorId(@PathVariable Long id) {
        ComissaoResponse comissao = comissaoService.buscarComissaoPorId(id);
        return ResponseEntity.ok(comissao);
    }

    @GetMapping("/vendedores")
    @Operation(summary = "Listar vendedores com comissões", description = "Retorna lista de vendedores que possuem comissões")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VENDEDOR')")
    public ResponseEntity<List<Usuario>> listarVendedoresComComissoes() {
        List<Usuario> vendedores = comissaoService.listarVendedoresComComissoes();
        return ResponseEntity.ok(vendedores);
    }
}
