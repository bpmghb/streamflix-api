package com.streamflix.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para criar lista de favoritos
 */
public record ListaFavoritosCreateDto(
        @NotBlank(message = "Nome da lista é obrigatório")
        @Size(max = 100, message = "Nome da lista deve ter no máximo 100 caracteres")
        String nome,

        String descricao,
        Boolean publica
) {
}
