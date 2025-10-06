package com.controleestoque.service;

import com.controleestoque.dto.ListResponse;
import com.controleestoque.dto.MovimentacaoFilters;
import com.controleestoque.dto.PaginationDto;
import com.controleestoque.dto.request.CreateMovimentacaoRequest;
import com.controleestoque.dto.request.EntradaEstoqueRequest;
import com.controleestoque.dto.request.SaidaEstoqueRequest;
import com.controleestoque.dto.response.MovimentacaoResponse;
import com.controleestoque.entity.MovimentacaoEstoque;
import com.controleestoque.entity.Produto;
import com.controleestoque.enums.TipoMovimentacao;
import com.controleestoque.exception.BusinessException;
import com.controleestoque.exception.ResourceNotFoundException;
import com.controleestoque.dto.mapper.MovimentacaoMapper;
import com.controleestoque.repository.MovimentacaoEstoqueRepository;
import com.controleestoque.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MovimentacaoEstoqueService {
    
    private final MovimentacaoEstoqueRepository movimentacaoRepository;
    private final ProdutoRepository produtoRepository;
    private final MovimentacaoMapper movimentacaoMapper;
    
    @Transactional(readOnly = true)
    public ListResponse<MovimentacaoResponse> listar(MovimentacaoFilters filters, PaginationDto pagination) {
        log.info("Listando movimentações com filtros: {}", filters);
        
        Sort sort = Sort.by(
            pagination.getOrderDirection().equalsIgnoreCase("asc") 
                ? Sort.Direction.ASC 
                : Sort.Direction.DESC, 
            pagination.getOrderBy()
        );
        
        Pageable pageable = PageRequest.of(
            pagination.getPage() - 1, 
            pagination.getLimit(), 
            sort
        );
        
        Page<MovimentacaoEstoque> page = movimentacaoRepository.findByFilters(
            filters.getProdutoId(),
            filters.getTipo(),
            filters.getDataInicio(),
            filters.getDataFim(),
            pageable
        );
        
        List<MovimentacaoResponse> movimentacoes = page.getContent()
            .stream()
            .map(movimentacaoMapper::toResponse)
            .toList();
        
        ListResponse.PaginationInfo paginationInfo = ListResponse.PaginationInfo.builder()
            .page(pagination.getPage())
            .limit(pagination.getLimit())
            .total(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .build();
            
        return ListResponse.<MovimentacaoResponse>builder()
            .data(movimentacoes)
            .pagination(paginationInfo)
            .build();
    }
    
    @Transactional(readOnly = true)
    public MovimentacaoResponse buscarPorId(Long id) {
        log.info("Buscando movimentação por ID: {}", id);
        
        MovimentacaoEstoque movimentacao = movimentacaoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Movimentação não encontrada com ID: " + id));
        
        return movimentacaoMapper.toResponse(movimentacao);
    }
    
    @Transactional(readOnly = true)
    public List<MovimentacaoResponse> listarPorProduto(Long produtoId) {
        log.info("Listando movimentações do produto ID: {}", produtoId);
        
        Produto produto = produtoRepository.findById(produtoId)
            .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + produtoId));
        
        List<MovimentacaoEstoque> movimentacoes = movimentacaoRepository
            .findByProdutoOrderByDataDesc(produto);
        
        return movimentacoes.stream()
            .map(movimentacaoMapper::toResponse)
            .toList();
    }
    
    public MovimentacaoResponse registrarEntrada(EntradaEstoqueRequest request) {
        log.info("Registrando entrada de estoque: {}", request);
        
        Produto produto = produtoRepository.findById(request.getProdutoId())
            .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + request.getProdutoId()));
        
        // Criar movimentação de entrada
        MovimentacaoEstoque movimentacao = MovimentacaoEstoque.builder()
            .produto(produto)
            .tipo(TipoMovimentacao.ENTRADA)
            .quantidade(request.getQuantidade())
            .motivo(request.getMotivo() != null ? request.getMotivo() : "Entrada de estoque")
            .data(LocalDateTime.now())
            .build();
        
        MovimentacaoEstoque movimentacaoSalva = movimentacaoRepository.save(movimentacao);
        
        // Atualizar estoque do produto
        atualizarEstoqueProduto(produto, request.getQuantidade(), TipoMovimentacao.ENTRADA);
        
        // Atualizar preço de custo se fornecido
        if (request.getPrecoCusto() != null && request.getPrecoCusto().compareTo(produto.getPrecoCusto()) != 0) {
            produto.setPrecoCusto(request.getPrecoCusto());
            produtoRepository.save(produto);
        }
        
        log.info("Entrada de estoque registrada com sucesso. Produto: {}, Quantidade: {}", 
                produto.getNome(), request.getQuantidade());
        
        return movimentacaoMapper.toResponse(movimentacaoSalva);
    }
    
    public MovimentacaoResponse registrarSaida(SaidaEstoqueRequest request) {
        log.info("Registrando saída de estoque: {}", request);
        
        Produto produto = produtoRepository.findById(request.getProdutoId())
            .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + request.getProdutoId()));
        
        // Verificar se há estoque suficiente
        if (produto.getEstoqueAtual() < request.getQuantidade()) {
            throw new BusinessException(
                String.format("Estoque insuficiente. Disponível: %d, Solicitado: %d", 
                    produto.getEstoqueAtual(), request.getQuantidade())
            );
        }
        
        // Criar movimentação de saída
        MovimentacaoEstoque movimentacao = MovimentacaoEstoque.builder()
            .produto(produto)
            .tipo(TipoMovimentacao.SAIDA)
            .quantidade(request.getQuantidade())
            .motivo(request.getMotivo() != null ? request.getMotivo() : "Saída de estoque")
            .data(LocalDateTime.now())
            .build();
        
        MovimentacaoEstoque movimentacaoSalva = movimentacaoRepository.save(movimentacao);
        
        // Atualizar estoque do produto
        atualizarEstoqueProduto(produto, request.getQuantidade(), TipoMovimentacao.SAIDA);
        
        log.info("Saída de estoque registrada com sucesso. Produto: {}, Quantidade: {}", 
                produto.getNome(), request.getQuantidade());
        
        return movimentacaoMapper.toResponse(movimentacaoSalva);
    }
    
    public MovimentacaoResponse criar(CreateMovimentacaoRequest request) {
        log.info("Criando movimentação: {}", request);
        
        Produto produto = produtoRepository.findById(request.getProdutoId())
            .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + request.getProdutoId()));
        
        MovimentacaoEstoque movimentacao = movimentacaoMapper.toEntity(request);
        movimentacao.setProduto(produto);
        movimentacao.setData(LocalDateTime.now());
        
        MovimentacaoEstoque movimentacaoSalva = movimentacaoRepository.save(movimentacao);
        
        // Atualizar estoque do produto
        atualizarEstoqueProduto(produto, request.getQuantidade(), request.getTipo());
        
        log.info("Movimentação criada com sucesso. ID: {}", movimentacaoSalva.getId());
        
        return movimentacaoMapper.toResponse(movimentacaoSalva);
    }
    
    private void atualizarEstoqueProduto(Produto produto, Integer quantidade, TipoMovimentacao tipo) {
        int estoqueAtual = produto.getEstoqueAtual();
        int novoEstoque;
        
        switch (tipo) {
            case ENTRADA:
                novoEstoque = estoqueAtual + quantidade;
                break;
            case SAIDA:
                novoEstoque = estoqueAtual - quantidade;
                if (novoEstoque < 0) {
                    throw new BusinessException("Estoque insuficiente para esta operação");
                }
                break;
            case AJUSTE:
                novoEstoque = quantidade; // Para ajuste, a quantidade é o novo valor do estoque
                break;
            default:
                throw new BusinessException("Tipo de movimentação inválido: " + tipo);
        }
        
        produto.setEstoqueAtual(novoEstoque);
        produtoRepository.save(produto);
        
        log.info("Estoque do produto {} atualizado de {} para {}", 
                produto.getNome(), estoqueAtual, novoEstoque);
    }
}
