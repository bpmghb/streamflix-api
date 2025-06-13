package com.streamflix.api.dto;

/**
 * DTO para ranking de popularidade
 */
public record FilmeRankingDto(
        Long id,
        String titulo,
        String genero,
        Long contadorAcessos,
        Integer posicaoRanking
) {
}
