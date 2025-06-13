package com.streamflix.api.mapper;

import com.streamflix.api.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommonMapper {

    /**
     * Converter Page para PageResponseDto
     */
    public <T> PageResponseDto<T> toPageResponseDto(Page<T> page) {
        return new PageResponseDto<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }

    /**
     * Criar EstatisticasDto
     */
    public EstatisticasDto toEstatisticasDto(
            Long totalFilmes,
            Long totalUsuarios,
            Long totalAvaliacoes,
            Long totalListasFavoritos,
            Double mediaGeralAvaliacoes) {

        return new EstatisticasDto(
                totalFilmes != null ? totalFilmes : 0L,
                totalUsuarios != null ? totalUsuarios : 0L,
                totalAvaliacoes != null ? totalAvaliacoes : 0L,
                totalListasFavoritos != null ? totalListasFavoritos : 0L,
                mediaGeralAvaliacoes != null ? Math.round(mediaGeralAvaliacoes * 100.0) / 100.0 : 0.0
        );
    }

    /**
     * Processar dados de ranking de gêneros do repository
     */
    public GeneroRankingDto toGeneroRankingDto(Object[] row) {
        String genero = (String) row[0];
        Long totalFilmes = ((Number) row[1]).longValue();
        Long totalAcessos = row.length > 2 ? ((Number) row[2]).longValue() : 0L;
        Double mediaAvaliacoes = row.length > 3 ? ((Number) row[3]).doubleValue() : 0.0;

        return new GeneroRankingDto(
                genero,
                totalFilmes,
                totalAcessos,
                Math.round(mediaAvaliacoes * 100.0) / 100.0
        );
    }

    /**
     * Converter lista de Object[] para GeneroRankingDto
     */
    public List<GeneroRankingDto> toGeneroRankingDtoList(List<Object[]> rows) {
        return rows.stream()
                .map(this::toGeneroRankingDto)
                .toList();
    }

    /**
     * Criar DashboardAdminDto
     */
    public DashboardAdminDto toDashboardAdminDto(
            EstatisticasDto estatisticas,
            List<FilmeRankingDto> topFilmesPopulares,
            List<GeneroRankingDto> rankingGeneros,
            List<UsuarioResumoDto> usuariosRecentes) {

        return new DashboardAdminDto(
                estatisticas,
                topFilmesPopulares,
                rankingGeneros,
                usuariosRecentes
        );
    }

    /**
     * Criar ApiResponseDto de sucesso
     */
    public <T> ApiResponseDto<T> toSuccessResponse(T data) {
        return ApiResponseDto.success(data);
    }

    /**
     * Criar ApiResponseDto de sucesso com mensagem customizada
     */
    public <T> ApiResponseDto<T> toSuccessResponse(String message, T data) {
        return ApiResponseDto.success(message, data);
    }

    /**
     * Criar ApiResponseDto de erro
     */
    public <T> ApiResponseDto<T> toErrorResponse(String message) {
        return ApiResponseDto.error(message);
    }

    /**
     * Criar OperationResponseDto de sucesso
     */
    public OperationResponseDto toSuccessOperation(String message) {
        return OperationResponseDto.success(message);
    }

    /**
     * Criar OperationResponseDto de erro
     */
    public OperationResponseDto toErrorOperation(String message) {
        return OperationResponseDto.error(message);
    }

    /**
     * Converter filtros de busca para objeto
     */
    public FilmeFiltroDto toFilmeFiltroDto(
            String titulo,
            String genero,
            Integer anoLancamento,
            String diretor,
            Boolean orderByPopularidade,
            Integer page,
            Integer size) {

        return new FilmeFiltroDto(
                titulo,
                genero,
                anoLancamento,
                diretor,
                orderByPopularidade != null ? orderByPopularidade : false,
                page != null ? page : 0,
                size != null ? size : 20
        );
    }
}