package com.controleestoque.service;

import com.controleestoque.dto.ListResponse;
import com.controleestoque.dto.PaginationDto;
import com.controleestoque.dto.VendaFilters;
import com.controleestoque.dto.mapper.VendaMapper;
import com.controleestoque.dto.request.CreateVendaRequest;
import com.controleestoque.dto.request.UpdateVendaRequest;
import com.controleestoque.dto.request.SaidaEstoqueRequest;
import com.controleestoque.dto.response.VendaResponse;
import com.controleestoque.entity.Produto;
import com.controleestoque.entity.Usuario;
import com.controleestoque.entity.Venda;
import com.controleestoque.enums.Role;
import com.controleestoque.exception.BusinessException;
import com.controleestoque.exception.ResourceNotFoundException;
import com.controleestoque.repository.ProdutoRepository;
import com.controleestoque.repository.UsuarioRepository;
import com.controleestoque.repository.VendaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;
    private final VendaMapper vendaMapper;
    private final MovimentacaoEstoqueService movimentacaoEstoqueService;
    
    private static final BigDecimal PERCENTUAL_COMISSAO_PADRAO = new BigDecimal("5.00"); // 5%

    /**
     * Cria uma nova venda e automaticamente dá baixa no estoque
     */
    @Transactional
    public VendaResponse criar(CreateVendaRequest request) {
        log.info("Criando nova venda: {}", request);
        
        // Validar produto
        Produto produto = produtoRepository.findById(request.getProdutoId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + request.getProdutoId()));
        
        // Validar vendedor
        Usuario vendedor = usuarioRepository.findById(request.getVendedorId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendedor não encontrado com ID: " + request.getVendedorId()));
        
        // Verificar se o usuário é vendedor
        if (!vendedor.getRole().equals(Role.VENDEDOR)) {
            throw new BusinessException("O usuário deve ter o papel de VENDEDOR para realizar vendas");
        }
        
        // Verificar se há estoque suficiente
        if (produto.getEstoqueAtual() < request.getQuantidade()) {
            throw new BusinessException(
                String.format("Estoque insuficiente. Disponível: %d, Solicitado: %d", 
                    produto.getEstoqueAtual(), request.getQuantidade())
            );
        }
        
        // Calcular comissão
        BigDecimal percentualComissao = request.getPercentualComissao() != null ? 
            request.getPercentualComissao() : PERCENTUAL_COMISSAO_PADRAO;
        
        BigDecimal comissao = request.getValorVenda()
                .multiply(percentualComissao)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        
        // Criar venda
        Venda venda = Venda.builder()
                .produto(produto)
                .vendedor(vendedor)
                .quantidade(request.getQuantidade())
                .valorVenda(request.getValorVenda())
                .comissao(comissao)
                .mercadoLivreId(request.getMercadoLivreId())
                .data(LocalDateTime.now())
                .build();
        
        Venda vendaSalva = vendaRepository.save(venda);
        log.info("Venda criada com sucesso. ID: {}", vendaSalva.getId());
        
        // Dar baixa automática no estoque
        try {
            SaidaEstoqueRequest saidaRequest = SaidaEstoqueRequest.builder()
                    .produtoId(request.getProdutoId())
                    .quantidade(request.getQuantidade())
                    .motivo("Venda realizada - ID: " + vendaSalva.getId())
                    .build();
            
            movimentacaoEstoqueService.registrarSaida(saidaRequest);
            log.info("Baixa automática no estoque realizada para a venda ID: {}", vendaSalva.getId());
            
        } catch (Exception e) {
            log.error("Erro ao dar baixa no estoque para a venda ID: {}. Erro: {}", vendaSalva.getId(), e.getMessage());
            throw new BusinessException("Erro ao processar baixa no estoque: " + e.getMessage());
        }
        
        return vendaMapper.toResponse(vendaSalva);
    }

    /**
     * Lista vendas com filtros e paginação
     */
    @Transactional(readOnly = true)
    public ListResponse<VendaResponse> listar(VendaFilters filters, PaginationDto pagination) {
        log.info("Listando vendas com filtros: {}", filters);
        
        Sort sort = Sort.by(Sort.Direction.fromString(pagination.getOrderDirection()), pagination.getOrderBy());
        Pageable pageable = PageRequest.of(pagination.getPage() - 1, pagination.getLimit(), sort);

        Page<Venda> vendasPage = vendaRepository.findByFilters(
                filters.getProdutoId(),
                filters.getVendedorId(),
                filters.getDataInicio(),
                filters.getDataFim(),
                pageable
        );

        var vendas = vendasPage.getContent()
                .stream()
                .map(vendaMapper::toResponse)
                .toList();

        ListResponse.PaginationInfo paginationInfo = ListResponse.PaginationInfo.builder()
                .page(pagination.getPage())
                .limit(pagination.getLimit())
                .total(vendasPage.getTotalElements())
                .totalPages(vendasPage.getTotalPages())
                .build();

        return ListResponse.<VendaResponse>builder()
                .data(vendas)
                .pagination(paginationInfo)
                .build();
    }

    /**
     * Busca venda por ID
     */
    @Transactional(readOnly = true)
    public VendaResponse buscarPorId(Long id) {
        log.info("Buscando venda por ID: {}", id);
        
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada com ID: " + id));
        
        return vendaMapper.toResponse(venda);
    }

    /**
     * Atualiza uma venda existente
     */
    @Transactional
    public VendaResponse atualizar(Long id, UpdateVendaRequest request) {
        log.info("Atualizando venda ID: {} com dados: {}", id, request);
        
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada com ID: " + id));
        
        // Validar produto
        Produto produto = produtoRepository.findById(request.getProdutoId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + request.getProdutoId()));
        
        // Validar vendedor
        Usuario vendedor = usuarioRepository.findById(request.getVendedorId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendedor não encontrado com ID: " + request.getVendedorId()));
        
        // Verificar se o usuário é vendedor
        if (!vendedor.getRole().equals(Role.VENDEDOR)) {
            throw new BusinessException("O usuário deve ter o papel de VENDEDOR para realizar vendas");
        }
        
        // Calcular nova comissão
        BigDecimal percentualComissao = request.getPercentualComissao() != null ? 
            request.getPercentualComissao() : PERCENTUAL_COMISSAO_PADRAO;
        
        BigDecimal comissao = request.getValorVenda()
                .multiply(percentualComissao)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        
        // Atualizar venda
        venda.setProduto(produto);
        venda.setVendedor(vendedor);
        venda.setQuantidade(request.getQuantidade());
        venda.setValorVenda(request.getValorVenda());
        venda.setComissao(comissao);
        venda.setMercadoLivreId(request.getMercadoLivreId());
        
        Venda vendaAtualizada = vendaRepository.save(venda);
        log.info("Venda atualizada com sucesso. ID: {}", vendaAtualizada.getId());
        
        return vendaMapper.toResponse(vendaAtualizada);
    }

    /**
     * Exclui uma venda
     */
    @Transactional
    public void excluir(Long id) {
        log.info("Excluindo venda ID: {}", id);
        
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada com ID: " + id));
        
        // TODO: Implementar lógica para devolver estoque se necessário
        // Por enquanto, apenas exclui a venda
        // Em um sistema real, você poderia querer dar entrada no estoque novamente
        
        vendaRepository.delete(venda);
        log.info("Venda excluída com sucesso. ID: {}", id);
    }

    /**
     * Busca vendas por vendedor
     */
    @Transactional(readOnly = true)
    public ListResponse<VendaResponse> buscarVendasPorVendedor(Long vendedorId, PaginationDto pagination) {
        log.info("Buscando vendas para vendedor ID: {}", vendedorId);
        
        Usuario vendedor = usuarioRepository.findById(vendedorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendedor não encontrado com ID: " + vendedorId));

        Sort sort = Sort.by(Sort.Direction.fromString(pagination.getOrderDirection()), pagination.getOrderBy());
        Pageable pageable = PageRequest.of(pagination.getPage() - 1, pagination.getLimit(), sort);

        Page<Venda> vendasPage = vendaRepository.findByVendedorOrderByDataDesc(vendedor, pageable);

        var vendas = vendasPage.getContent()
                .stream()
                .map(vendaMapper::toResponse)
                .toList();

        ListResponse.PaginationInfo paginationInfo = ListResponse.PaginationInfo.builder()
                .page(pagination.getPage())
                .limit(pagination.getLimit())
                .total(vendasPage.getTotalElements())
                .totalPages(vendasPage.getTotalPages())
                .build();

        return ListResponse.<VendaResponse>builder()
                .data(vendas)
                .pagination(paginationInfo)
                .build();
    }
}
