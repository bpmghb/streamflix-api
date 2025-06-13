package com.streamflix.api.dto;

import java.time.LocalDateTime;

/**
 * DTO para resposta da lista de favoritos
 */
public record ListaFavoritosResponseDto(
        Long id,
        String nome,
        String descricao,
        Boolean publica,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao,
        UsuarioResumoDto usuario,
        Long totalFilmes
) {
}
