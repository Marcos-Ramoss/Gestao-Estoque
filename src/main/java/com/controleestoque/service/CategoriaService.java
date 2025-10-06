package com.controleestoque.service;

import com.controleestoque.dto.request.CreateCategoriaRequest;
import com.controleestoque.dto.response.CategoriaResponse;
import com.controleestoque.dto.mapper.CategoriaMapper;
import com.controleestoque.entity.Categoria;
import com.controleestoque.exception.BusinessException;
import com.controleestoque.exception.ResourceNotFoundException;
import com.controleestoque.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoriaService {
    
    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;
    
    @Transactional(readOnly = true)
    public List<CategoriaResponse> listarTodas() {
        log.info("Listando todas as categorias");
        
        return categoriaRepository.findAllAtivasOrderByNome()
                .stream()
                .map(categoriaMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public CategoriaResponse buscarPorId(Long id) {
        log.info("Buscando categoria por ID: {}", id);
        
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", "id", id));
        
        return categoriaMapper.toResponse(categoria);
    }
    
    public CategoriaResponse criar(CreateCategoriaRequest request) {
        log.info("Criando nova categoria: {}", request.getNome());
        
        // Verificar se nome já existe
        if (categoriaRepository.existsByNome(request.getNome())) {
            throw new BusinessException("Nome da categoria já está em uso");
        }
        
        Categoria categoria = categoriaMapper.toEntity(request);
        Categoria savedCategoria = categoriaRepository.save(categoria);
        
        log.info("Categoria criada com sucesso: {}", savedCategoria.getId());
        return categoriaMapper.toResponse(savedCategoria);
    }
    
    public CategoriaResponse atualizar(Long id, CreateCategoriaRequest request) {
        log.info("Atualizando categoria ID: {}", id);
        
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", "id", id));
        
        // Verificar se nome já existe em outra categoria
        if (!request.getNome().equals(categoria.getNome()) && 
            categoriaRepository.existsByNome(request.getNome())) {
            throw new BusinessException("Nome da categoria já está em uso");
        }
        
        categoria.setNome(request.getNome());
        categoria.setDescricao(request.getDescricao());
        
        Categoria savedCategoria = categoriaRepository.save(categoria);
        
        log.info("Categoria atualizada com sucesso: {}", savedCategoria.getId());
        return categoriaMapper.toResponse(savedCategoria);
    }
    
    public void excluir(Long id) {
        log.info("Excluindo categoria ID: {}", id);
        
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", "id", id));
        
        // Verificar se há produtos associados
        if (!categoria.getProdutos().isEmpty()) {
            throw new BusinessException("Não é possível excluir categoria com produtos associados");
        }
        
        categoriaRepository.delete(categoria);
        
        log.info("Categoria excluída com sucesso: {}", id);
    }
    
    public CategoriaResponse ativar(Long id) {
        log.info("Ativando categoria ID: {}", id);
        
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", "id", id));
        
        categoria.setAtivo(true);
        Categoria savedCategoria = categoriaRepository.save(categoria);
        
        log.info("Categoria ativada com sucesso: {}", savedCategoria.getId());
        return categoriaMapper.toResponse(savedCategoria);
    }
    
    public CategoriaResponse desativar(Long id) {
        log.info("Desativando categoria ID: {}", id);
        
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", "id", id));
        
        categoria.setAtivo(false);
        Categoria savedCategoria = categoriaRepository.save(categoria);
        
        log.info("Categoria desativada com sucesso: {}", savedCategoria.getId());
        return categoriaMapper.toResponse(savedCategoria);
    }
}




