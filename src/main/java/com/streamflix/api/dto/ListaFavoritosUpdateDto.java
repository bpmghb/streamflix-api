package com.streamflix.api.dto;

import jakarta.validation.constraints.Size;

/**
 * DTO para atualizar lista de favoritos
 */
public record ListaFavoritosUpdateDto(
        @Size(max = 100, message = "Nome da lista deve ter no máximo 100 caracteres")
        String nome,

        String descricao,
        Boolean publica
) {
}
