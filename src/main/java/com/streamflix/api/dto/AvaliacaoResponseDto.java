package com.streamflix.api.dto;

import java.time.LocalDateTime;

/**
 * DTO para resposta da avaliação
 */
public record AvaliacaoResponseDto(
        Long id,
        Integer nota,
        String comentario,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao,
        UsuarioResumoDto usuario,
        FilmeResumoDto filme
) {
}
