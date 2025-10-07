package com.controleestoque.service;

import com.controleestoque.dto.response.*;
import com.controleestoque.entity.Produto;
import com.controleestoque.entity.Venda;
import com.controleestoque.repository.MovimentacaoEstoqueRepository;
import com.controleestoque.repository.ProdutoRepository;
import com.controleestoque.repository.VendaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RelatorioService {

    private final ProdutoRepository produtoRepository;
    private final VendaRepository vendaRepository;
    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    /**
     * Relatório de produtos com estoque baixo
     */
    @Transactional(readOnly = true)
    public List<RelatorioEstoqueBaixoResponse> relatorioEstoqueBaixo() {
        log.info("Gerando relatório de estoque baixo");
        
        List<Produto> produtos = produtoRepository.findByAtivoTrue();
        
        return produtos.stream()
                .filter(produto -> produto.getEstoqueAtual() <= produto.getEstoqueMinimo())
                .map(produto -> {
                    Integer diferenca = produto.getEstoqueMinimo() - produto.getEstoqueAtual();
                    String status = calcularStatusEstoque(produto.getEstoqueAtual(), produto.getEstoqueMinimo());
                    
                    return RelatorioEstoqueBaixoResponse.builder()
                            .id(produto.getId())
                            .nome(produto.getNome())
                            .sku(produto.getSku())
                            .categoria(produto.getCategoria() != null ? produto.getCategoria().getNome() : "Sem categoria")
                            .estoqueAtual(produto.getEstoqueAtual())
                            .estoqueMinimo(produto.getEstoqueMinimo())
                            .precoVenda(produto.getPrecoVenda())
                            .diferenca(diferenca)
                            .status(status)
                            .build();
                })
                .sorted((p1, p2) -> Integer.compare(p1.getDiferenca(), p2.getDiferenca()))
                .collect(Collectors.toList());
    }

    /**
     * Relatório de resumo do estoque
     */
    @Transactional(readOnly = true)
    public RelatorioResumoEstoqueResponse relatorioResumoEstoque() {
        log.info("Gerando relatório de resumo do estoque");
        
        List<Produto> produtos = produtoRepository.findByAtivoTrue();
        
        int totalProdutos = produtos.size();
        int produtosComEstoque = (int) produtos.stream().filter(p -> p.getEstoqueAtual() > 0).count();
        int produtosSemEstoque = (int) produtos.stream().filter(p -> p.getEstoqueAtual() == 0).count();
        int produtosEstoqueBaixo = (int) produtos.stream().filter(p -> p.getEstoqueAtual() <= p.getEstoqueMinimo() && p.getEstoqueAtual() > 0).count();
        int produtosEstoqueCritico = (int) produtos.stream().filter(p -> p.getEstoqueAtual() == 0 || p.getEstoqueAtual() < (p.getEstoqueMinimo() * 0.5)).count();
        
        BigDecimal valorTotalEstoque = produtos.stream()
                .map(p -> p.getPrecoVenda().multiply(BigDecimal.valueOf(p.getEstoqueAtual())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal valorMedioProduto = totalProdutos > 0 ? 
                valorTotalEstoque.divide(BigDecimal.valueOf(totalProdutos), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        
        long totalMovimentacoes = movimentacaoEstoqueRepository.count();
        
        return RelatorioResumoEstoqueResponse.builder()
                .totalProdutos(totalProdutos)
                .produtosComEstoque(produtosComEstoque)
                .produtosSemEstoque(produtosSemEstoque)
                .produtosEstoqueBaixo(produtosEstoqueBaixo)
                .produtosEstoqueCritico(produtosEstoqueCritico)
                .valorTotalEstoque(valorTotalEstoque)
                .valorMedioProduto(valorMedioProduto)
                .totalMovimentacoes((int) totalMovimentacoes)
                .build();
    }

    /**
     * Relatório de vendas por período
     */
    @Transactional(readOnly = true)
    public RelatorioVendasPeriodoResponse relatorioVendasPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        log.info("Gerando relatório de vendas para período de {} a {}", dataInicio, dataFim);
        
        List<Venda> vendas = vendaRepository.findAll().stream()
                .filter(venda -> {
                    if (dataInicio != null && venda.getData().isBefore(dataInicio)) return false;
                    if (dataFim != null && venda.getData().isAfter(dataFim)) return false;
                    return true;
                })
                .collect(Collectors.toList());
        
        int totalVendas = vendas.size();
        int totalProdutosVendidos = vendas.stream().mapToInt(Venda::getQuantidade).sum();
        BigDecimal valorTotalVendas = vendas.stream().map(Venda::getValorVenda).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal comissaoTotal = vendas.stream().map(Venda::getComissao).reduce(BigDecimal.ZERO, BigDecimal::add);
        long totalVendedores = vendas.stream().map(Venda::getVendedor).distinct().count();
        BigDecimal ticketMedio = totalVendas > 0 ? 
                valorTotalVendas.divide(BigDecimal.valueOf(totalVendas), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        
        return RelatorioVendasPeriodoResponse.builder()
                .periodoInicio(dataInicio)
                .periodoFim(dataFim)
                .totalVendas(totalVendas)
                .totalProdutosVendidos(totalProdutosVendidos)
                .valorTotalVendas(valorTotalVendas)
                .comissaoTotal(comissaoTotal)
                .totalVendedores((int) totalVendedores)
                .ticketMedio(ticketMedio)
                .build();
    }

    /**
     * Relatório de produtos mais vendidos
     */
    @Transactional(readOnly = true)
    public List<ProdutoMaisVendidoResponse> relatorioProdutosMaisVendidos(LocalDateTime dataInicio, LocalDateTime dataFim, int limite) {
        log.info("Gerando relatório de produtos mais vendidos para período de {} a {}", dataInicio, dataFim);
        
        List<Venda> vendas = vendaRepository.findAll().stream()
                .filter(venda -> {
                    if (dataInicio != null && venda.getData().isBefore(dataInicio)) return false;
                    if (dataFim != null && venda.getData().isAfter(dataFim)) return false;
                    return true;
                })
                .collect(Collectors.toList());
        
        return vendas.stream()
                .collect(Collectors.groupingBy(Venda::getProduto))
                .entrySet().stream()
                .map(entry -> {
                    Produto produto = entry.getKey();
                    List<Venda> vendasProduto = entry.getValue();
                    
                    int quantidadeVendida = vendasProduto.stream().mapToInt(Venda::getQuantidade).sum();
                    BigDecimal valorTotalVendas = vendasProduto.stream().map(Venda::getValorVenda).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal valorMedioVenda = vendasProduto.size() > 0 ? 
                            valorTotalVendas.divide(BigDecimal.valueOf(vendasProduto.size()), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                    int numeroVendas = vendasProduto.size();
                    
                    return ProdutoMaisVendidoResponse.builder()
                            .produtoId(produto.getId())
                            .nomeProduto(produto.getNome())
                            .skuProduto(produto.getSku())
                            .categoriaProduto(produto.getCategoria() != null ? produto.getCategoria().getNome() : "Sem categoria")
                            .quantidadeVendida(quantidadeVendida)
                            .valorTotalVendas(valorTotalVendas)
                            .valorMedioVenda(valorMedioVenda)
                            .numeroVendas(numeroVendas)
                            .estoqueAtual(produto.getEstoqueAtual())
                            .build();
                })
                .sorted((p1, p2) -> Integer.compare(p2.getQuantidadeVendida(), p1.getQuantidadeVendida()))
                .limit(limite)
                .collect(Collectors.toList());
    }

    /**
     * Relatório de movimentações de estoque por período
     */
    @Transactional(readOnly = true)
    public List<RelatorioMovimentacaoResponse> relatorioMovimentacoesEstoque(LocalDateTime dataInicio, LocalDateTime dataFim) {
        log.info("Gerando relatório de movimentações de estoque para período de {} a {}", dataInicio, dataFim);
        
        return movimentacaoEstoqueRepository.findAll().stream()
                .filter(movimentacao -> {
                    if (dataInicio != null && movimentacao.getData().isBefore(dataInicio)) return false;
                    if (dataFim != null && movimentacao.getData().isAfter(dataFim)) return false;
                    return true;
                })
                .sorted((m1, m2) -> m2.getData().compareTo(m1.getData()))
                .map(movimentacao -> RelatorioMovimentacaoResponse.builder()
                        .id(movimentacao.getId())
                        .produtoId(movimentacao.getProduto().getId())
                        .nomeProduto(movimentacao.getProduto().getNome())
                        .skuProduto(movimentacao.getProduto().getSku())
                        .categoriaProduto(movimentacao.getProduto().getCategoria() != null ? 
                                movimentacao.getProduto().getCategoria().getNome() : "Sem categoria")
                        .tipo(movimentacao.getTipo())
                        .quantidade(movimentacao.getQuantidade())
                        .motivo(movimentacao.getMotivo())
                        .data(movimentacao.getData())
                        .createdAt(movimentacao.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    private String calcularStatusEstoque(Integer estoqueAtual, Integer estoqueMinimo) {
        if (estoqueAtual == 0) {
            return "CRÍTICO";
        } else if (estoqueAtual <= estoqueMinimo * 0.5) {
            return "CRÍTICO";
        } else if (estoqueAtual <= estoqueMinimo) {
            return "BAIXO";
        } else {
            return "ADEQUADO";
        }
    }
}
