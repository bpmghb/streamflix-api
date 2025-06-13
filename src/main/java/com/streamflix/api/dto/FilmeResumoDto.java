package com.streamflix.api.dto;

/**
 * DTO para resposta resumida do filme (para listagens)
 */
public record FilmeResumoDto(
        Long id,
        String titulo,
        String genero,
        Integer anoLancamento,
        String diretor,
        String urlPoster,
        Long contadorAcessos
) {
}
