package com.streamflix.api.dto;

import java.time.LocalDateTime;

/**
 * DTO para resposta resumida da avaliação
 */
public record AvaliacaoResumoDto(
        Long id,
        Integer nota,
        String comentario,
        LocalDateTime dataCriacao,
        UsuarioResumoDto usuario
) {
}
