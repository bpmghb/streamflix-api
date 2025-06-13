package com.streamflix.api.dto;

/**
 * DTO para resposta de erro de autenticação
 */
public record AuthErrorDto(
        String error,
        String message,
        long timestamp
) {
}
