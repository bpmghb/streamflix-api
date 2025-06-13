package com.streamflix.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para criar avaliação
 */
public record AvaliacaoCreateDto(
        @NotNull(message = "Nota é obrigatória")
        @Min(value = 1, message = "Nota deve ser entre 1 e 5")
        @Max(value = 5, message = "Nota deve ser entre 1 e 5")
        Integer nota,

        String comentario,

        @NotNull(message = "ID do filme é obrigatório")
        Long filmeId
) {
}
