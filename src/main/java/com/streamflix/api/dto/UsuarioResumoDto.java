package com.streamflix.api.dto;

import com.streamflix.api.entity.PerfilUsuario;

/**
 * DTO para resposta resumida
 */
public record UsuarioResumoDto(
        Long id,
        String nomeUsuario,
        PerfilUsuario perfil
) {
}
