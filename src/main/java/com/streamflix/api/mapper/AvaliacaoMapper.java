package com.streamflix.api.mapper;

import com.streamflix.api.dto.*;
import com.streamflix.api.entity.Avaliacao;
import com.streamflix.api.entity.Filme;
import com.streamflix.api.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class AvaliacaoMapper {

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private FilmeMapper filmeMapper;

    /**
     * Converter AvaliacaoCreateDto para Avaliacao entity
     */
    public Avaliacao toEntity(AvaliacaoCreateDto dto, Usuario usuario, Filme filme) {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setNota(dto.nota());
        avaliacao.setComentario(dto.comentario());
        avaliacao.setUsuario(usuario);
        avaliacao.setFilme(filme);
        return avaliacao;
    }

    /**
     * Aplicar AvaliacaoUpdateDto em Avaliacao entity existente
     */
    public void updateEntity(Avaliacao avaliacao, AvaliacaoUpdateDto dto) {
        if (dto.nota() != null) {
            avaliacao.setNota(dto.nota());
        }
        if (dto.comentario() != null) {
            avaliacao.setComentario(dto.comentario());
        }
        // Nota: usuário e filme não podem ser alterados via update
    }

    /**
     * Converter Avaliacao entity para AvaliacaoResponseDto
     */
    public AvaliacaoResponseDto toResponseDto(Avaliacao avaliacao) {
        return new AvaliacaoResponseDto(
                avaliacao.getId(),
                avaliacao.getNota(),
                avaliacao.getComentario(),
                avaliacao.getDataCriacao(),
                avaliacao.getDataAtualizacao(),
                usuarioMapper.toResumoDto(avaliacao.getUsuario()),
                filmeMapper.toResumoDto(avaliacao.getFilme())
        );
    }

    /**
     * Converter Avaliacao entity para AvaliacaoResumoDto
     */
    public AvaliacaoResumoDto toResumoDto(Avaliacao avaliacao) {
        return new AvaliacaoResumoDto(
                avaliacao.getId(),
                avaliacao.getNota(),
                avaliacao.getComentario(),
                avaliacao.getDataCriacao(),
                usuarioMapper.toResumoDto(avaliacao.getUsuario())
        );
    }

    /**
     * Criar AvaliacaoEstatisticasDto a partir de dados estatísticos
     */
    public AvaliacaoEstatisticasDto toEstatisticasDto(
            Filme filme,
            Double mediaNotas,
            Long totalAvaliacoes,
            Map<Integer, Long> distribuicaoNotas) {

        return new AvaliacaoEstatisticasDto(
                filme.getId(),
                filme.getTitulo(),
                mediaNotas != null ? Math.round(mediaNotas * 100.0) / 100.0 : 0.0,
                totalAvaliacoes != null ? totalAvaliacoes : 0L,
                distribuicaoNotas.getOrDefault(1, 0L),
                distribuicaoNotas.getOrDefault(2, 0L),
                distribuicaoNotas.getOrDefault(3, 0L),
                distribuicaoNotas.getOrDefault(4, 0L),
                distribuicaoNotas.getOrDefault(5, 0L)
        );
    }

    /**
     * Processar estatísticas de avaliações vindas do repository
     */
    public AvaliacaoEstatisticasDto processarEstatisticasRepository(
            Filme filme,
            Double mediaNotas,
            Long totalAvaliacoes,
            List<Object[]> distribuicaoNotas) {

        // Converter List<Object[]> para Map<Integer, Long>
        Map<Integer, Long> distribuicaoMap = new java.util.HashMap<>();
        distribuicaoNotas.forEach(row -> {
            Integer nota = (Integer) row[0];
            Long count = (Long) row[1];
            distribuicaoMap.put(nota, count);
        });

        return toEstatisticasDto(filme, mediaNotas, totalAvaliacoes, distribuicaoMap);
    }

    /**
     * Converter lista de Avaliacao entities para AvaliacaoResponseDto
     */
    public List<AvaliacaoResponseDto> toResponseDtoList(List<Avaliacao> avaliacoes) {
        return avaliacoes.stream()
                .map(this::toResponseDto)
                .toList();
    }

    /**
     * Converter lista de Avaliacao entities para AvaliacaoResumoDto
     */
    public List<AvaliacaoResumoDto> toResumoDtoList(List<Avaliacao> avaliacoes) {
        return avaliacoes.stream()
                .map(this::toResumoDto)
                .toList();
    }
}