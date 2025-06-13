package com.streamflix.api.dto;

/**
 * DTO para resposta de autenticação
 */
public record AuthResponseDto(
        String token,
        String tipo, // "Bearer"
        Long expiresIn, // Tempo em segundos
        UsuarioResponseDto usuario
) {
}
