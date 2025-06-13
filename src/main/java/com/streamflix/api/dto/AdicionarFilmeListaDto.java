package com.streamflix.api.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO para adicionar filme à lista
 */
public record AdicionarFilmeListaDto(
        @NotNull(message = "ID do filme é obrigatório")
        Long filmeId
) {
}
