package com.streamflix.api.dto;

/**
 * DTO para estatísticas de avaliação de um filme
 */
public record AvaliacaoEstatisticasDto(
        Long filmeId,
        String tituloFilme,
        Double mediaNotas,
        Long totalAvaliacoes,
        Long avaliacoesCom1Estrela,
        Long avaliacoesCom2Estrelas,
        Long avaliacoesCom3Estrelas,
        Long avaliacoesCom4Estrelas,
        Long avaliacoesCom5Estrelas
) {
}
