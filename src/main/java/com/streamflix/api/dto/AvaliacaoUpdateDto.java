package com.streamflix.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * DTO para atualizar avaliação
 */
public record AvaliacaoUpdateDto(
        @Min(value = 1, message = "Nota deve ser entre 1 e 5")
        @Max(value = 5, message = "Nota deve ser entre 1 e 5")
        Integer nota,

        String comentario
) {}

