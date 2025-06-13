package com.streamflix.api.dto;

import java.time.LocalDateTime;

/**
 * DTO para filme na lista (com data de adição)
 */
public record FilmeNaListaDto(
        Long id,
        String titulo,
        String genero,
        Integer anoLancamento,
        String urlPoster,
        LocalDateTime dataAdicao
) {
}
