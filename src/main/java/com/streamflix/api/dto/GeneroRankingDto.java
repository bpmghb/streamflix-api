package com.streamflix.api.dto;

/**
 * DTO para ranking de gêneros
 */
public record GeneroRankingDto(
        String genero,
        Long totalFilmes,
        Long totalAcessos,
        Double mediaAvaliacoes
) {
}
