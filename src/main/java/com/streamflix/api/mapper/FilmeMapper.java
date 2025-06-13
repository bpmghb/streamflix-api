package com.streamflix.api.mapper;

import com.streamflix.api.dto.*;
import com.streamflix.api.entity.Filme;
import com.streamflix.api.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class FilmeMapper {

    @Autowired
    private UsuarioMapper usuarioMapper;

    /**
     * Converter FilmeCreateDto para Filme entity
     */
    public Filme toEntity(FilmeCreateDto dto) {
        Filme filme = new Filme();
        filme.setTitulo(dto.titulo());
        filme.setDescricao(dto.descricao());
        filme.setGenero(dto.genero());
        filme.setAnoLancamento(dto.anoLancamento());
        filme.setDuracao(dto.duracao());
        filme.setDiretor(dto.diretor());
        filme.setUrlPoster(dto.urlPoster());
        filme.setAtivo(true);
        filme.setContadorAcessos(0L); // Inicializar contador de acessos
        return filme;
    }

    /**
     * Aplicar FilmeUpdateDto em Filme entity existente
     */
    public void updateEntity(Filme filme, FilmeUpdateDto dto) {
        if (dto.titulo() != null) {
            filme.setTitulo(dto.titulo());
        }
        if (dto.descricao() != null) {
            filme.setDescricao(dto.descricao());
        }
        if (dto.genero() != null) {
            filme.setGenero(dto.genero());
        }
        if (dto.anoLancamento() != null) {
            filme.setAnoLancamento(dto.anoLancamento());
        }
        if (dto.duracao() != null) {
            filme.setDuracao(dto.duracao());
        }
        if (dto.diretor() != null) {
            filme.setDiretor(dto.diretor());
        }
        if (dto.urlPoster() != null) {
            filme.setUrlPoster(dto.urlPoster());
        }
        if (dto.ativo() != null) {
            filme.setAtivo(dto.ativo());
        }
        // Nota: contadorAcessos não é atualizado via DTO
    }

    /**
     * Converter Filme entity para FilmeResponseDto
     */
    public FilmeResponseDto toResponseDto(Filme filme) {
        return new FilmeResponseDto(
                filme.getId(),
                filme.getTitulo(),
                filme.getDescricao(),
                filme.getGenero(),
                filme.getAnoLancamento(),
                filme.getDuracao(),
                filme.getDiretor(),
                filme.getUrlPoster(),
                filme.getAtivo(),
                filme.getContadorAcessos(),
                filme.getDataCriacao(),
                filme.getDataAtualizacao(),
                filme.getCriadoPor() != null ? usuarioMapper.toResumoDto(filme.getCriadoPor()) : null
        );
    }

    /**
     * Converter Filme entity para FilmeResumoDto
     */
    public FilmeResumoDto toResumoDto(Filme filme) {
        return new FilmeResumoDto(
                filme.getId(),
                filme.getTitulo(),
                filme.getGenero(),
                filme.getAnoLancamento(),
                filme.getDiretor(),
                filme.getUrlPoster(),
                filme.getContadorAcessos()
        );
    }

    /**
     * Converter Filme entity para FilmeDetalhesDto (com estatísticas)
     */
    public FilmeDetalhesDto toDetalhesDto(Filme filme, Double mediaAvaliacoes, Long totalAvaliacoes) {
        return new FilmeDetalhesDto(
                filme.getId(),
                filme.getTitulo(),
                filme.getDescricao(),
                filme.getGenero(),
                filme.getAnoLancamento(),
                filme.getDuracao(),
                filme.getDiretor(),
                filme.getUrlPoster(),
                filme.getContadorAcessos(),
                mediaAvaliacoes != null ? mediaAvaliacoes : 0.0,
                totalAvaliacoes != null ? totalAvaliacoes : 0L,
                filme.getDataCriacao(),
                filme.getCriadoPor() != null ? usuarioMapper.toResumoDto(filme.getCriadoPor()) : null
        );
    }

    /**
     * Converter Filme entity para FilmeRankingDto (com posição no ranking)
     */
    public FilmeRankingDto toRankingDto(Filme filme, Integer posicao) {
        return new FilmeRankingDto(
                filme.getId(),
                filme.getTitulo(),
                filme.getGenero(),
                filme.getContadorAcessos(),
                posicao
        );
    }

    /**
     * Converter lista de filmes para ranking com posições
     */
    public List<FilmeRankingDto> toRankingDtoList(List<Filme> filmes) {
        AtomicInteger posicao = new AtomicInteger(1);
        return filmes.stream()
                .map(filme -> toRankingDto(filme, posicao.getAndIncrement()))
                .toList();
    }

    /**
     * Converter Filme entity para FilmeNaListaDto
     */
    public FilmeNaListaDto toFilmeNaListaDto(Filme filme, java.time.LocalDateTime dataAdicao) {
        return new FilmeNaListaDto(
                filme.getId(),
                filme.getTitulo(),
                filme.getGenero(),
                filme.getAnoLancamento(),
                filme.getUrlPoster(),
                dataAdicao
        );
    }

    /**
     * Converter lista de Filme entities para FilmeResponseDto
     */
    public List<FilmeResponseDto> toResponseDtoList(List<Filme> filmes) {
        return filmes.stream()
                .map(this::toResponseDto)
                .toList();
    }

    /**
     * Converter lista de Filme entities para FilmeResumoDto
     */
    public List<FilmeResumoDto> toResumoDtoList(List<Filme> filmes) {
        return filmes.stream()
                .map(this::toResumoDto)
                .toList();
    }
}