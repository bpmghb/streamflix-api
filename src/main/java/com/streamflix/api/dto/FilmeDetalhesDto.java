package com.streamflix.api.dto;

import java.time.LocalDateTime;

/**
 * DTO para detalhes do filme com estatísticas
 */
public record FilmeDetalhesDto(
        Long id,
        String titulo,
        String descricao,
        String genero,
        Integer anoLancamento,
        Integer duracao,
        String diretor,
        String urlPoster,
        Long contadorAcessos,
        Double mediaAvaliacoes,
        Long totalAvaliacoes,
        LocalDateTime dataCriacao,
        UsuarioResumoDto criadoPor
) {
}
