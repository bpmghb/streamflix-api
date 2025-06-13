package com.streamflix.api.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para resposta completa com filmes
 */
public record ListaFavoritosComFilmesDto(
        Long id,
        String nome,
        String descricao,
        Boolean publica,
        LocalDateTime dataCriacao,
        UsuarioResumoDto usuario,
        List<FilmeNaListaDto> filmes
) {
}
