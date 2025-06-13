package com.streamflix.api.dto;

/**
 * DTO para estatísticas gerais
 */
public record EstatisticasDto(
        Long totalFilmes,
        Long totalUsuarios,
        Long totalAvaliacoes,
        Long totalListasFavoritos,
        Double mediaGeralAvaliacoes
) {
}
