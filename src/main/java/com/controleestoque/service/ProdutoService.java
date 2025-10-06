package com.controleestoque.service;

import com.controleestoque.dto.PaginationDto;
import com.controleestoque.dto.ListResponse;
import com.controleestoque.dto.request.CreateProdutoRequest;
import com.controleestoque.dto.response.ProdutoResponse;
import com.controleestoque.dto.mapper.ProdutoMapper;
import com.controleestoque.entity.Categoria;
import com.controleestoque.entity.Produto;
import com.controleestoque.exception.BusinessException;
import com.controleestoque.exception.ResourceNotFoundException;
import com.controleestoque.repository.CategoriaRepository;
import com.controleestoque.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProdutoService {
    
    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProdutoMapper produtoMapper;
    
    @Transactional(readOnly = true)
    public ListResponse<ProdutoResponse> listar(String search, Long categoriaId, Boolean ativo, PaginationDto pagination) {
        log.info("Listando produtos com filtros - search: {}, categoriaId: {}, ativo: {}", search, categoriaId, ativo);
        
        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(pagination.getOrderDirection()) 
                        ? Sort.Direction.DESC 
                        : Sort.Direction.ASC,
                pagination.getOrderBy()
        );
        
        Pageable pageable = PageRequest.of(
                pagination.getPage() - 1, 
                pagination.getLimit(), 
                sort
        );
        
        Page<Produto> produtosPage = produtoRepository.findByFilters(search, categoriaId, ativo, pageable);
        
        List<ProdutoResponse> produtos = produtosPage.getContent()
                .stream()
                .map(produtoMapper::toResponse)
                .collect(Collectors.toList());
        
        ListResponse.PaginationInfo paginationInfo = ListResponse.PaginationInfo.builder()
                .page(pagination.getPage())
                .limit(pagination.getLimit())
                .total(produtosPage.getTotalElements())
                .totalPages(produtosPage.getTotalPages())
                .build();
        
        return ListResponse.<ProdutoResponse>builder()
                .data(produtos)
                .pagination(paginationInfo)
                .build();
    }
    
    @Transactional(readOnly = true)
    public List<ProdutoResponse> listarTodos() {
        log.info("Listando todos os produtos");
        
        return produtoRepository.findAllAtivosOrderByNome()
                .stream()
                .map(produtoMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ProdutoResponse> listarProdutosComEstoqueBaixo() {
        log.info("Listando produtos com estoque baixo");
        
        return produtoRepository.findProdutosComEstoqueBaixo()
                .stream()
                .map(produtoMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public ProdutoResponse buscarPorId(Long id) {
        log.info("Buscando produto por ID: {}", id);
        
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto", "id", id));
        
        return produtoMapper.toResponse(produto);
    }
    
    @Transactional(readOnly = true)
    public ProdutoResponse buscarPorSku(String sku) {
        log.info("Buscando produto por SKU: {}", sku);
        
        Produto produto = produtoRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Produto", "SKU", sku));
        
        return produtoMapper.toResponse(produto);
    }
    
    public ProdutoResponse criar(CreateProdutoRequest request) {
        log.info("Criando novo produto: {}", request.getNome());
        
        // Verificar se SKU já existe
        if (produtoRepository.existsBySku(request.getSku())) {
            throw new BusinessException("SKU já está em uso");
        }
        
        // Buscar categoria
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", "id", request.getCategoriaId()));
        
        if (!categoria.getAtivo()) {
            throw new BusinessException("Categoria está inativa");
        }
        
        Produto produto = produtoMapper.toEntity(request, categoria);
        Produto savedProduto = produtoRepository.save(produto);
        
        log.info("Produto criado com sucesso: {}", savedProduto.getId());
        return produtoMapper.toResponse(savedProduto);
    }
    
    public ProdutoResponse atualizar(Long id, CreateProdutoRequest request) {
        log.info("Atualizando produto ID: {}", id);
        
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto", "id", id));
        
        // Verificar se SKU já existe em outro produto
        if (!request.getSku().equals(produto.getSku()) && 
            produtoRepository.existsBySku(request.getSku())) {
            throw new BusinessException("SKU já está em uso");
        }
        
        // Buscar categoria
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", "id", request.getCategoriaId()));
        
        produto.setNome(request.getNome());
        produto.setDescricao(request.getDescricao());
        produto.setSku(request.getSku());
        produto.setPrecoCusto(request.getPrecoCusto());
        produto.setPrecoVenda(request.getPrecoVenda());
        produto.setEstoqueMinimo(request.getEstoqueMinimo() != null ? request.getEstoqueMinimo() : 0);
        produto.setCategoria(categoria);
        
        Produto savedProduto = produtoRepository.save(produto);
        
        log.info("Produto atualizado com sucesso: {}", savedProduto.getId());
        return produtoMapper.toResponse(savedProduto);
    }
    
    public void excluir(Long id) {
        log.info("Excluindo produto ID: {}", id);
        
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto", "id", id));
        
        // Verificar se há movimentações ou vendas associadas
        if (!produto.getMovimentacoes().isEmpty() || !produto.getVendas().isEmpty()) {
            throw new BusinessException("Não é possível excluir produto com movimentações ou vendas associadas");
        }
        
        produtoRepository.delete(produto);
        
        log.info("Produto excluído com sucesso: {}", id);
    }
    
    public ProdutoResponse ativar(Long id) {
        log.info("Ativando produto ID: {}", id);
        
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto", "id", id));
        
        produto.setAtivo(true);
        Produto savedProduto = produtoRepository.save(produto);
        
        log.info("Produto ativado com sucesso: {}", savedProduto.getId());
        return produtoMapper.toResponse(savedProduto);
    }
    
    public ProdutoResponse desativar(Long id) {
        log.info("Desativando produto ID: {}", id);
        
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto", "id", id));
        
        produto.setAtivo(false);
        Produto savedProduto = produtoRepository.save(produto);
        
        log.info("Produto desativado com sucesso: {}", savedProduto.getId());
        return produtoMapper.toResponse(savedProduto);
    }
}




