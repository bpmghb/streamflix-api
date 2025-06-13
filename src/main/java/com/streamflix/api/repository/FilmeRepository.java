package com.streamflix.api.repository;

import com.streamflix.api.entity.Filme;
import com.streamflix.api.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FilmeRepository extends JpaRepository<Filme, Long> {

    // Buscar apenas filmes ativos (para usuários comuns)
    List<Filme> findByAtivoTrue();

    Page<Filme> findByAtivoTrue(Pageable pageable);

    // Buscar por título (case-insensitive)
    List<Filme> findByTituloContainingIgnoreCase(String titulo);

    // Buscar por gênero
    List<Filme> findByGeneroIgnoreCase(String genero);

    // Buscar por ano de lançamento
    List<Filme> findByAnoLancamento(Integer ano);

    // Buscar por diretor
    List<Filme> findByDiretorContainingIgnoreCase(String diretor);

    // Buscar filmes criados por um usuário específico
    List<Filme> findByCriadoPor(Usuario criadoPor);

    // REQUISITO OBRIGATÓRIO: Ranking de Popularidade
    // Ordenar por contador de acessos (mais acessados primeiro)
    @Query("SELECT f FROM Filme f WHERE f.ativo = true ORDER BY f.contadorAcessos DESC")
    List<Filme> findByAtivoTrueOrderByContadorAcessosDesc();

    @Query("SELECT f FROM Filme f WHERE f.ativo = true ORDER BY f.contadorAcessos DESC")
    Page<Filme> findByAtivoTrueOrderByContadorAcessosDesc(Pageable pageable);

    // Top N filmes mais populares
    @Query("SELECT f FROM Filme f WHERE f.ativo = true ORDER BY f.contadorAcessos DESC")
    List<Filme> findTopFilmesByPopularidade(Pageable pageable);

    // Buscar filmes com filtros e ordenação por popularidade
    @Query("SELECT f FROM Filme f WHERE f.ativo = true " +
            "AND (:genero IS NULL OR UPPER(f.genero) = UPPER(:genero)) " +
            "AND (:ano IS NULL OR f.anoLancamento = :ano) " +
            "ORDER BY f.contadorAcessos DESC")
    List<Filme> findFilmesComFiltrosOrderByPopularidade(
            @Param("genero") String genero,
            @Param("ano") Integer ano
    );

    // Buscar por ID apenas se ativo (para usuários comuns)
    Optional<Filme> findByIdAndAtivoTrue(Long id);

    // Contar filmes por gênero
    @Query("SELECT f.genero, COUNT(f) FROM Filme f WHERE f.ativo = true GROUP BY f.genero")
    List<Object[]> countFilmesByGenero();
}