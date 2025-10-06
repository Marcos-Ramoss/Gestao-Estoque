package com.controleestoque.service;

import com.controleestoque.dto.ComissaoFilters;
import com.controleestoque.dto.ListResponse;
import com.controleestoque.dto.PaginationDto;
import com.controleestoque.dto.mapper.ComissaoMapper;
import com.controleestoque.dto.request.CalcularComissaoRequest;
import com.controleestoque.dto.response.ComissaoResponse;
import com.controleestoque.dto.response.RelatorioComissaoResponse;
import com.controleestoque.entity.Usuario;
import com.controleestoque.entity.Venda;
import com.controleestoque.exception.ResourceNotFoundException;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComissaoService {

    private final VendaRepository vendaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ComissaoMapper comissaoMapper;
    
    private static final BigDecimal PERCENTUAL_COMISSAO_PADRAO = new BigDecimal("5.00"); // 5%

    /**
     * Calcula o valor da comissão baseado no valor da venda e percentual
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularComissao(CalcularComissaoRequest request) {
        log.info("Calculando comissão para valor de venda: {} com percentual: {}", 
                request.getValorVenda(), request.getPercentualComissao());
        
        BigDecimal comissao = request.getValorVenda()
                .multiply(request.getPercentualComissao())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        
        log.info("Comissão calculada: {}", comissao);
        return comissao;
    }

    /**
     * Calcula comissão usando o percentual padrão (5%)
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularComissao(BigDecimal valorVenda) {
        return calcularComissao(CalcularComissaoRequest.builder()
                .valorVenda(valorVenda)
                .percentualComissao(PERCENTUAL_COMISSAO_PADRAO)
                .build());
    }

    /**
     * Lista comissões com filtros e paginação
     */
    @Transactional(readOnly = true)
    public ListResponse<ComissaoResponse> listarComissoes(ComissaoFilters filters, PaginationDto pagination) {
        log.info("Listando comissões com filtros: {}", filters);
        
        Sort sort = Sort.by(Sort.Direction.fromString(pagination.getOrderDirection()), pagination.getOrderBy());
        Pageable pageable = PageRequest.of(pagination.getPage() - 1, pagination.getLimit(), sort);

        Page<Venda> vendasPage = vendaRepository.findByFilters(
                filters.getProdutoId(),
                filters.getVendedorId(),
                filters.getDataInicio(),
                filters.getDataFim(),
                pageable
        );

        List<ComissaoResponse> comissoes = vendasPage.getContent()
                .stream()
                .map(comissaoMapper::toResponse)
                .toList();

        ListResponse.PaginationInfo paginationInfo = ListResponse.PaginationInfo.builder()
                .page(pagination.getPage())
                .limit(pagination.getLimit())
                .total(vendasPage.getTotalElements())
                .totalPages(vendasPage.getTotalPages())
                .build();

        return ListResponse.<ComissaoResponse>builder()
                .data(comissoes)
                .pagination(paginationInfo)
                .build();
    }

    /**
     * Busca comissões por vendedor
     */
    @Transactional(readOnly = true)
    public ListResponse<ComissaoResponse> buscarComissoesPorVendedor(Long vendedorId, PaginationDto pagination) {
        log.info("Buscando comissões para vendedor ID: {}", vendedorId);
        
        Usuario vendedor = usuarioRepository.findById(vendedorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendedor não encontrado com ID: " + vendedorId));

        Sort sort = Sort.by(Sort.Direction.fromString(pagination.getOrderDirection()), pagination.getOrderBy());
        Pageable pageable = PageRequest.of(pagination.getPage() - 1, pagination.getLimit(), sort);

        Page<Venda> vendasPage = vendaRepository.findByVendedorOrderByDataDesc(vendedor, pageable);

        List<ComissaoResponse> comissoes = vendasPage.getContent()
                .stream()
                .map(comissaoMapper::toResponse)
                .toList();

        ListResponse.PaginationInfo paginationInfo = ListResponse.PaginationInfo.builder()
                .page(pagination.getPage())
                .limit(pagination.getLimit())
                .total(vendasPage.getTotalElements())
                .totalPages(vendasPage.getTotalPages())
                .build();

        return ListResponse.<ComissaoResponse>builder()
                .data(comissoes)
                .pagination(paginationInfo)
                .build();
    }

    /**
     * Gera relatório de comissões por vendedor em um período
     */
    @Transactional(readOnly = true)
    public RelatorioComissaoResponse gerarRelatorioComissoesPorVendedor(Long vendedorId, 
                                                                       LocalDateTime dataInicio, 
                                                                       LocalDateTime dataFim) {
        log.info("Gerando relatório de comissões para vendedor ID: {} no período de {} a {}", 
                vendedorId, dataInicio, dataFim);
        
        Usuario vendedor = usuarioRepository.findById(vendedorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendedor não encontrado com ID: " + vendedorId));

        // Buscar vendas do vendedor no período
        ComissaoFilters filters = ComissaoFilters.builder()
                .vendedorId(vendedorId)
                .dataInicio(dataInicio)
                .dataFim(dataFim)
                .build();

        PaginationDto pagination = PaginationDto.builder()
                .page(1)
                .limit(1000) // Buscar todas as vendas do período
                .orderBy("data")
                .orderDirection("desc")
                .build();

        ListResponse<ComissaoResponse> comissoesResponse = listarComissoes(filters, pagination);
        List<ComissaoResponse> comissoes = comissoesResponse.getData();

        // Calcular totais
        int totalVendas = comissoes.size();
        BigDecimal valorTotalVendas = comissoes.stream()
                .map(ComissaoResponse::getValorVenda)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal comissaoTotal = comissoes.stream()
                .map(ComissaoResponse::getComissao)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal percentualComissao = BigDecimal.ZERO;
        if (valorTotalVendas.compareTo(BigDecimal.ZERO) > 0) {
            percentualComissao = comissaoTotal
                    .multiply(BigDecimal.valueOf(100))
                    .divide(valorTotalVendas, 2, RoundingMode.HALF_UP);
        }

        return RelatorioComissaoResponse.builder()
                .vendedorId(vendedor.getId())
                .nomeVendedor(vendedor.getNome())
                .emailVendedor(vendedor.getEmail())
                .periodoInicio(dataInicio)
                .periodoFim(dataFim)
                .totalVendas(totalVendas)
                .valorTotalVendas(valorTotalVendas)
                .comissaoTotal(comissaoTotal)
                .percentualComissao(percentualComissao)
                .detalhesVendas(comissoes)
                .build();
    }

    /**
     * Busca comissão por ID
     */
    @Transactional(readOnly = true)
    public ComissaoResponse buscarComissaoPorId(Long id) {
        log.info("Buscando comissão por ID: {}", id);
        
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada com ID: " + id));
        
        return comissaoMapper.toResponse(venda);
    }

    /**
     * Lista todos os vendedores que têm comissões
     */
    @Transactional(readOnly = true)
    public List<Usuario> listarVendedoresComComissoes() {
        log.info("Listando vendedores com comissões");
        
        return vendaRepository.findAll()
                .stream()
                .map(Venda::getVendedor)
                .distinct()
                .collect(Collectors.toList());
    }
}
