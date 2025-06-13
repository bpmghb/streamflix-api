package com.streamflix.api.repository;

import com.streamflix.api.entity.ListaFavoritosFilmes;
import com.streamflix.api.entity.ListaFavoritos;
import com.streamflix.api.entity.Filme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ListaFavoritosFilmesRepository extends JpaRepository<ListaFavoritosFilmes, Long> {

    // Buscar filmes de uma lista específica
    List<ListaFavoritosFilmes> findByListaFavoritos(ListaFavoritos listaFavoritos);

    // Buscar listas que contêm um filme específico
    List<ListaFavoritosFilmes> findByFilme(Filme filme);

    // Verificar se um filme já está em uma lista específica
    boolean existsByListaFavoritosAndFilme(ListaFavoritos listaFavoritos, Filme filme);

    // Buscar relação específica entre lista e filme
    Optional<ListaFavoritosFilmes> findByListaFavoritosAndFilme(ListaFavoritos listaFavoritos, Filme filme);

    // Contar quantos filmes tem em uma lista
    Long countByListaFavoritos(ListaFavoritos listaFavoritos);

    // Buscar filmes de uma lista ordenados por data de adição (mais recentes primeiro)
    List<ListaFavoritosFilmes> findByListaFavoritosOrderByDataAdicaoDesc(ListaFavoritos listaFavoritos);

    // Filmes mais adicionados às listas (ranking de filmes favoritos)
    @Query("SELECT lff.filme, COUNT(lff) as total FROM ListaFavoritosFilmes lff " +
            "WHERE lff.filme.ativo = true " +
            "GROUP BY lff.filme " +
            "ORDER BY total DESC")
    List<Object[]> findFilmesMaisAdicionadosListas();
}