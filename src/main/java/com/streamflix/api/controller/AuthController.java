package com.streamflix.api.controller;

import com.streamflix.api.config.JwtService;
import com.streamflix.api.dto.*;
import com.streamflix.api.entity.Usuario;
import com.streamflix.api.mapper.UsuarioMapper;
import com.streamflix.api.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private JwtService jwtService;

    /**
     * Login de usuário
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        try {
            // Buscar usuário por login (nome de usuário ou email)
            Optional<Usuario> usuarioOpt = usuarioService.getByLoginAndAtivo(loginRequest.login());

            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthErrorDto("INVALID_CREDENTIALS", "Credenciais inválidas", System.currentTimeMillis()));
            }

            Usuario usuario = usuarioOpt.get();

            // Validar senha
            if (!usuarioService.validarSenha(loginRequest.senha(), usuario.getSenha())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthErrorDto("INVALID_CREDENTIALS", "Credenciais inválidas", System.currentTimeMillis()));
            }

            // Gerar token JWT
            String token = jwtService.generateTokenWithUserInfo(
                    usuario.getNomeUsuario(),
                    usuario.getPerfil().name()
            );

            // Criar resposta
            AuthResponseDto response = new AuthResponseDto(
                    token,
                    "Bearer",
                    86400L, // 24 horas em segundos
                    usuarioMapper.toResponseDto(usuario)
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthErrorDto("INTERNAL_ERROR", "Erro interno do servidor", System.currentTimeMillis()));
        }
    }

    /**
     * Registro de novo usuário
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDto registerRequest) {
        try {
            // Converter DTO para entity
            Usuario novoUsuario = usuarioMapper.toEntity(registerRequest);

            // Criar usuário
            Usuario usuarioCriado = usuarioService.create(novoUsuario);

            // Gerar token JWT
            String token = jwtService.generateTokenWithUserInfo(
                    usuarioCriado.getNomeUsuario(),
                    usuarioCriado.getPerfil().name()
            );

            // Criar resposta
            AuthResponseDto response = new AuthResponseDto(
                    token,
                    "Bearer",
                    86400L, // 24 horas em segundos
                    usuarioMapper.toResponseDto(usuarioCriado)
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthErrorDto("REGISTRATION_ERROR", e.getMessage(), System.currentTimeMillis()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthErrorDto("INTERNAL_ERROR", "Erro interno do servidor", System.currentTimeMillis()));
        }
    }

    /**
     * Validar token (endpoint opcional para verificar se token é válido)
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthErrorDto("INVALID_TOKEN", "Token inválido ou ausente", System.currentTimeMillis()));
            }

            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);

            // Buscar usuário
            Optional<Usuario> usuarioOpt = usuarioService.getByLoginAndAtivo(username);

            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthErrorDto("USER_NOT_FOUND", "Usuário não encontrado", System.currentTimeMillis()));
            }

            // Validar token
            if (!jwtService.isTokenValid(token, username)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthErrorDto("INVALID_TOKEN", "Token inválido ou expirado", System.currentTimeMillis()));
            }

            return ResponseEntity.ok(new OperationResponseDto(
                    true,
                    "Token válido",
                    java.time.LocalDateTime.now()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthErrorDto("TOKEN_ERROR", "Erro ao validar token", System.currentTimeMillis()));
        }
    }

    /**
     * Renovar token (endpoint opcional)
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthErrorDto("INVALID_TOKEN", "Token inválido ou ausente", System.currentTimeMillis()));
            }

            String oldToken = authHeader.substring(7);

            // Verificar se token está prestes a expirar
            if (jwtService.isTokenExpiringSoon(oldToken) && !jwtService.isTokenExpired(oldToken)) {
                String username = jwtService.extractUsername(oldToken);
                String perfil = jwtService.extractUserProfile(oldToken);

                // Gerar novo token
                String newToken = jwtService.generateTokenWithUserInfo(username, perfil);

                // Buscar dados do usuário
                Optional<Usuario> usuarioOpt = usuarioService.getByLoginAndAtivo(username);
                if (usuarioOpt.isPresent()) {
                    AuthResponseDto response = new AuthResponseDto(
                            newToken,
                            "Bearer",
                            86400L,
                            usuarioMapper.toResponseDto(usuarioOpt.get())
                    );

                    return ResponseEntity.ok(response);
                }
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthErrorDto("REFRESH_NOT_NEEDED", "Token ainda válido, renovação não necessária", System.currentTimeMillis()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthErrorDto("REFRESH_ERROR", "Erro ao renovar token", System.currentTimeMillis()));
        }
    }
}