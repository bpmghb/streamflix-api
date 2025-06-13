package com.streamflix.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * DTO para atualizar filme
 */
public record FilmeUpdateDto(
        @Size(max = 255, message = "Título deve ter no máximo 255 caracteres")
        String titulo,

        String descricao,
        String genero,

        @Min(value = 1900, message = "Ano de lançamento deve ser maior que 1900")
        Integer anoLancamento,

        @Min(value = 1, message = "Duração deve ser maior que 0")
        Integer duracao,

        String diretor,
        String urlPoster,
        Boolean ativo
) {}

