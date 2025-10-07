package com.controleestoque.controller;

import com.controleestoque.dto.response.*;
import com.controleestoque.service.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/relatorios")
@RequiredArgsConstructor
@Validated
@Tag(name = "Relatórios", description = "Operações relacionadas a relatórios do sistema")
public class RelatorioController {

    private final RelatorioService relatorioService;

    // ========== RELATÓRIOS DE ESTOQUE ==========

    @GetMapping("/estoque/baixo")
    @Operation(summary = "Relatório de estoque baixo", description = "Retorna produtos com estoque abaixo do mínimo configurado")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VENDEDOR') or hasRole('VISUALIZADOR')")
    public ResponseEntity<List<RelatorioEstoqueBaixoResponse>> relatorioEstoqueBaixo() {
        List<RelatorioEstoqueBaixoResponse> relatorio = relatorioService.relatorioEstoqueBaixo();
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/estoque/resumo")
    @Operation(summary = "Resumo do estoque", description = "Retorna resumo geral do estoque com estatísticas")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VENDEDOR') or hasRole('VISUALIZADOR')")
    public ResponseEntity<RelatorioResumoEstoqueResponse> relatorioResumoEstoque() {
        RelatorioResumoEstoqueResponse relatorio = relatorioService.relatorioResumoEstoque();
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/estoque/movimentacoes")
    @Operation(summary = "Relatório de movimentações de estoque", description = "Retorna movimentações de estoque por período")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VENDEDOR') or hasRole('VISUALIZADOR')")
    public ResponseEntity<List<RelatorioMovimentacaoResponse>> relatorioMovimentacoesEstoque(
            @Parameter(description = "Data de início (formato: yyyy-MM-dd)")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @Parameter(description = "Data de fim (formato: yyyy-MM-dd)")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        
        LocalDateTime inicio = dataInicio != null ? dataInicio.atStartOfDay() : null;
        LocalDateTime fim = dataFim != null ? dataFim.atTime(23, 59, 59) : null;
        
        List<RelatorioMovimentacaoResponse> movimentacoes = relatorioService.relatorioMovimentacoesEstoque(inicio, fim);
        return ResponseEntity.ok(movimentacoes);
    }

    // ========== RELATÓRIOS DE VENDAS ==========

    @GetMapping("/vendas/periodo")
    @Operation(summary = "Relatório de vendas por período", description = "Retorna estatísticas de vendas em um período")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VENDEDOR') or hasRole('VISUALIZADOR')")
    public ResponseEntity<RelatorioVendasPeriodoResponse> relatorioVendasPeriodo(
            @Parameter(description = "Data de início (formato: yyyy-MM-dd)")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @Parameter(description = "Data de fim (formato: yyyy-MM-dd)")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        
        LocalDateTime inicio = dataInicio != null ? dataInicio.atStartOfDay() : null;
        LocalDateTime fim = dataFim != null ? dataFim.atTime(23, 59, 59) : null;
        
        RelatorioVendasPeriodoResponse relatorio = relatorioService.relatorioVendasPeriodo(inicio, fim);
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/vendas/produtos-mais-vendidos")
    @Operation(summary = "Produtos mais vendidos", description = "Retorna ranking dos produtos mais vendidos em um período")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VENDEDOR') or hasRole('VISUALIZADOR')")
    public ResponseEntity<List<ProdutoMaisVendidoResponse>> relatorioProdutosMaisVendidos(
            @Parameter(description = "Data de início (formato: yyyy-MM-dd)")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @Parameter(description = "Data de fim (formato: yyyy-MM-dd)")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @Parameter(description = "Limite de produtos no ranking")
            @RequestParam(defaultValue = "10") int limite) {
        
        LocalDateTime inicio = dataInicio != null ? dataInicio.atStartOfDay() : null;
        LocalDateTime fim = dataFim != null ? dataFim.atTime(23, 59, 59) : null;
        
        List<ProdutoMaisVendidoResponse> produtos = relatorioService.relatorioProdutosMaisVendidos(inicio, fim, limite);
        return ResponseEntity.ok(produtos);
    }

    // ========== RELATÓRIOS FINANCEIROS ==========

    @GetMapping("/comissoes/total")
    @Operation(summary = "Total de comissões por período", description = "Retorna total de comissões em um período")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VENDEDOR')")
    public ResponseEntity<String> relatorioTotalComissoes(
            @Parameter(description = "Data de início (formato: yyyy-MM-dd)")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @Parameter(description = "Data de fim (formato: yyyy-MM-dd)")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        
        LocalDateTime inicio = dataInicio != null ? dataInicio.atStartOfDay() : null;
        LocalDateTime fim = dataFim != null ? dataFim.atTime(23, 59, 59) : null;
        
        RelatorioVendasPeriodoResponse relatorio = relatorioService.relatorioVendasPeriodo(inicio, fim);
        return ResponseEntity.ok("Total de comissões no período: R$ " + relatorio.getComissaoTotal());
    }

    @GetMapping("/receita/periodo")
    @Operation(summary = "Receita total por período", description = "Retorna receita total em um período")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VENDEDOR')")
    public ResponseEntity<String> relatorioReceitaPeriodo(
            @Parameter(description = "Data de início (formato: yyyy-MM-dd)")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @Parameter(description = "Data de fim (formato: yyyy-MM-dd)")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        
        LocalDateTime inicio = dataInicio != null ? dataInicio.atStartOfDay() : null;
        LocalDateTime fim = dataFim != null ? dataFim.atTime(23, 59, 59) : null;
        
        RelatorioVendasPeriodoResponse relatorio = relatorioService.relatorioVendasPeriodo(inicio, fim);
        return ResponseEntity.ok("Receita total no período: R$ " + relatorio.getValorTotalVendas());
    }
}
