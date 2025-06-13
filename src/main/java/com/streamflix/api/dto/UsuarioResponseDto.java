package com.streamflix.api.dto;

import com.streamflix.api.entity.PerfilUsuario;

import java.time.LocalDateTime;

/**
 * DTO para resposta (sem senha)
 */
public record UsuarioResponseDto(
        Long id,
        String nomeUsuario,
        String email,
        PerfilUsuario perfil,
        Boolean ativo,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {
}
