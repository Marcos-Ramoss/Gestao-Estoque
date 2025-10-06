package com.controleestoque.service;

import com.controleestoque.dto.PaginationDto;
import com.controleestoque.dto.UsuarioFilters;
import com.controleestoque.dto.ListResponse;
import com.controleestoque.dto.request.CreateUsuarioRequest;
import com.controleestoque.dto.request.UpdateUsuarioRequest;
import com.controleestoque.dto.response.UsuarioResponse;
import com.controleestoque.dto.mapper.UsuarioMapper;
import com.controleestoque.entity.Usuario;
import com.controleestoque.exception.BusinessException;
import com.controleestoque.exception.ResourceNotFoundException;
import com.controleestoque.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional(readOnly = true)
    public ListResponse<UsuarioResponse> listar(UsuarioFilters filters, PaginationDto pagination) {
        log.info("Listando usuários com filtros: {}", filters);
        
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
        
        Page<Usuario> usuariosPage = usuarioRepository.findByFilters(
                filters.getSearch(),
                filters.getRole(),
                filters.getAtivo(),
                pageable
        );
        
        List<UsuarioResponse> usuarios = usuariosPage.getContent()
                .stream()
                .map(usuarioMapper::toResponse)
                .collect(Collectors.toList());
        
        ListResponse.PaginationInfo paginationInfo = ListResponse.PaginationInfo.builder()
                .page(pagination.getPage())
                .limit(pagination.getLimit())
                .total(usuariosPage.getTotalElements())
                .totalPages(usuariosPage.getTotalPages())
                .build();
        
        return ListResponse.<UsuarioResponse>builder()
                .data(usuarios)
                .pagination(paginationInfo)
                .build();
    }
    
    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        log.info("Buscando usuário por ID: {}", id);
        
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", id));
        
        return usuarioMapper.toResponse(usuario);
    }
    
    public UsuarioResponse criar(CreateUsuarioRequest request) {
        log.info("Criando novo usuário: {}", request.getEmail());
        
        // Verificar se email já existe
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email já está em uso");
        }
        
        Usuario usuario = usuarioMapper.toEntity(request);
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        
        Usuario savedUsuario = usuarioRepository.save(usuario);
        
        log.info("Usuário criado com sucesso: {}", savedUsuario.getId());
        return usuarioMapper.toResponse(savedUsuario);
    }
    
    public UsuarioResponse atualizar(Long id, UpdateUsuarioRequest request) {
        log.info("Atualizando usuário ID: {}", id);
        
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", id));
        
        // Verificar se email já existe em outro usuário
        if (request.getEmail() != null && !request.getEmail().equals(usuario.getEmail())) {
            if (usuarioRepository.existsByEmail(request.getEmail())) {
                throw new BusinessException("Email já está em uso");
            }
        }
        
        usuario = usuarioMapper.updateEntity(usuario, request);
        Usuario savedUsuario = usuarioRepository.save(usuario);
        
        log.info("Usuário atualizado com sucesso: {}", savedUsuario.getId());
        return usuarioMapper.toResponse(savedUsuario);
    }
    
    public void excluir(Long id) {
        log.info("Excluindo usuário ID: {}", id);
        
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", id));
        
        usuarioRepository.delete(usuario);
        
        log.info("Usuário excluído com sucesso: {}", id);
    }
    
    public UsuarioResponse ativar(Long id) {
        log.info("Ativando usuário ID: {}", id);
        
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", id));
        
        usuario.setAtivo(true);
        Usuario savedUsuario = usuarioRepository.save(usuario);
        
        log.info("Usuário ativado com sucesso: {}", savedUsuario.getId());
        return usuarioMapper.toResponse(savedUsuario);
    }
    
    public UsuarioResponse desativar(Long id) {
        log.info("Desativando usuário ID: {}", id);
        
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", id));
        
        usuario.setAtivo(false);
        Usuario savedUsuario = usuarioRepository.save(usuario);
        
        log.info("Usuário desativado com sucesso: {}", savedUsuario.getId());
        return usuarioMapper.toResponse(savedUsuario);
    }
}




