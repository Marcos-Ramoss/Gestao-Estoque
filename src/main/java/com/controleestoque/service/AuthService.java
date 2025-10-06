package com.controleestoque.service;

import com.controleestoque.dto.request.LoginRequest;
import com.controleestoque.dto.response.LoginResponse;
import com.controleestoque.entity.Usuario;
import com.controleestoque.exception.BusinessException;
import com.controleestoque.repository.UsuarioRepository;
import com.controleestoque.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    
    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getSenha()
                    )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            String token = tokenProvider.generateToken(authentication);
            Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new BusinessException("Usuário não encontrado"));
            
            return LoginResponse.builder()
                    .token(token)
                    .id(usuario.getId())
                    .nome(usuario.getNome())
                    .email(usuario.getEmail())
                    .role(usuario.getRole())
                    .build();
                    
        } catch (Exception ex) {
            log.error("Erro no login para email: {}", request.getEmail(), ex);
            throw new BusinessException("Credenciais inválidas");
        }
    }
    
    public void logout() {
        SecurityContextHolder.clearContext();
        log.info("Usuário fez logout");
    }
}




