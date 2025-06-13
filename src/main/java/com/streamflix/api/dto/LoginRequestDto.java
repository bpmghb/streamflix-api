package com.streamflix.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para requisição de login
 */
public record LoginRequestDto(
        @NotBlank(message = "Login é obrigatório")
        String login, // Pode ser nome de usuário ou email

        @NotBlank(message = "Senha é obrigatória")
        String senha
) {
}
