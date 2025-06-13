package com.streamflix.api.dto;

/**
 * DTO para resposta resumida da lista
 */
public record ListaFavoritosResumoDto(
        Long id,
        String nome,
        Boolean publica,
        UsuarioResumoDto usuario,
        Long totalFilmes
) {}