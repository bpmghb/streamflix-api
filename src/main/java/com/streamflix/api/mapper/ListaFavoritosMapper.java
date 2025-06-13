package com.streamflix.api.mapper;

import com.streamflix.api.dto.*;
import com.streamflix.api.entity.ListaFavoritos;
import com.streamflix.api.entity.ListaFavoritosFilmes;
import com.streamflix.api.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListaFavoritosMapper {

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private FilmeMapper filmeMapper;

    /**
     * Converter ListaFavoritosCreateDto para ListaFavoritos entity
     */
    public ListaFavoritos toEntity(ListaFavoritosCreateDto dto, Usuario usuario) {
        ListaFavoritos lista = new ListaFavoritos();
        lista.setNome(dto.nome());
        lista.setDescricao(dto.descricao());
        lista.setPublica(dto.publica() != null ? dto.publica() : true);
        lista.setUsuario(usuario);
        return lista;
    }

    /**
     * Aplicar ListaFavoritosUpdateDto em ListaFavoritos entity existente
     */
    public void updateEntity(ListaFavoritos lista, ListaFavoritosUpdateDto dto) {
        if (dto.nome() != null) {
            lista.setNome(dto.nome());
        }
        if (dto.descricao() != null) {
            lista.setDescricao(dto.descricao());
        }
        if (dto.publica() != null) {
            lista.setPublica(dto.publica());
        }
        // Nota: usuário proprietário não pode ser alterado
    }

    /**
     * Converter ListaFavoritos entity para ListaFavoritosResponseDto
     */
    public ListaFavoritosResponseDto toResponseDto(ListaFavoritos lista, Long totalFilmes) {
        return new ListaFavoritosResponseDto(
                lista.getId(),
                lista.getNome(),
                lista.getDescricao(),
                lista.getPublica(),
                lista.getDataCriacao(),
                lista.getDataAtualizacao(),
                usuarioMapper.toResumoDto(lista.getUsuario()),
                totalFilmes != null ? totalFilmes : 0L
        );
    }

    /**
     * Converter ListaFavoritos entity para ListaFavoritosComFilmesDto
     */
    public ListaFavoritosComFilmesDto toComFilmesDto(
            ListaFavoritos lista,
            List<ListaFavoritosFilmes> filmesNaLista) {

        List<FilmeNaListaDto> filmesDto = filmesNaLista.stream()
                .map(relacao -> filmeMapper.toFilmeNaListaDto(
                        relacao.getFilme(),
                        relacao.getDataAdicao()
                ))
                .toList();

        return new ListaFavoritosComFilmesDto(
                lista.getId(),
                lista.getNome(),
                lista.getDescricao(),
                lista.getPublica(),
                lista.getDataCriacao(),
                usuarioMapper.toResumoDto(lista.getUsuario()),
                filmesDto
        );
    }

    /**
     * Converter ListaFavoritos entity para ListaFavoritosResumoDto
     */
    public ListaFavoritosResumoDto toResumoDto(ListaFavoritos lista, Long totalFilmes) {
        return new ListaFavoritosResumoDto(
                lista.getId(),
                lista.getNome(),
                lista.getPublica(),
                usuarioMapper.toResumoDto(lista.getUsuario()),
                totalFilmes != null ? totalFilmes : 0L
        );
    }

    /**
     * Converter lista de ListaFavoritos entities para ListaFavoritosResponseDto
     */
    public List<ListaFavoritosResponseDto> toResponseDtoList(
            List<ListaFavoritos> listas,
            java.util.Map<Long, Long> totalFilmesPorLista) {

        return listas.stream()
                .map(lista -> toResponseDto(lista, totalFilmesPorLista.get(lista.getId())))
                .toList();
    }

    /**
     * Converter lista de ListaFavoritos entities para ListaFavoritosResumoDto
     */
    public List<ListaFavoritosResumoDto> toResumoDtoList(
            List<ListaFavoritos> listas,
            java.util.Map<Long, Long> totalFilmesPorLista) {

        return listas.stream()
                .map(lista -> toResumoDto(lista, totalFilmesPorLista.get(lista.getId())))
                .toList();
    }

    /**
     * Versão simplificada sem contagem de filmes
     */
    public List<ListaFavoritosResponseDto> toResponseDtoListSimples(List<ListaFavoritos> listas) {
        return listas.stream()
                .map(lista -> toResponseDto(lista, 0L))
                .toList();
    }

    /**
     * Versão simplificada do resumo sem contagem de filmes
     */
    public List<ListaFavoritosResumoDto> toResumoDtoListSimples(List<ListaFavoritos> listas) {
        return listas.stream()
                .map(lista -> toResumoDto(lista, 0L))
                .toList();
    }
}