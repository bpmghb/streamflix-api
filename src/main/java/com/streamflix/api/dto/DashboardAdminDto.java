package com.streamflix.api.dto;

import java.util.List;

/**
 * DTO para dashboard do administrador
 */
public record DashboardAdminDto(
        EstatisticasDto estatisticas,
        List<FilmeRankingDto> topFilmesPopulares,
        List<GeneroRankingDto> rankingGeneros,
        List<UsuarioResumoDto> usuariosRecentes
) {
}
