package com.streamflix.api.dto;

import java.time.LocalDateTime;

/**
 * DTO para resposta completa do filme
 */
public record FilmeResponseDto(
        Long id,
        String titulo,
        String descricao,
        String genero,
        Integer anoLancamento,
        Integer duracao,
        String diretor,
        String urlPoster,
        Boolean ativo,
        Long contadorAcessos, // Para ranking de popularidade
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao,
        UsuarioResumoDto criadoPor
) {
}
