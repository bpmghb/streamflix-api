package com.streamflix.api.dto;

/**
 * DTO para filtros de busca de filmes
 */
public record FilmeFiltroDto(
        String titulo,
        String genero,
        Integer anoLancamento,
        String diretor,
        Boolean orderByPopularidade,
        Integer page,
        Integer size
) {
}
