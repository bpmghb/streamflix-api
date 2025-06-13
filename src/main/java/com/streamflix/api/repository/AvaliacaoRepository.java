package com.streamflix.api.repository;

import com.streamflix.api.entity.Avaliacao;
import com.streamflix.api.entity.Filme;
import com.streamflix.api.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    // Buscar avaliações de um filme específico
    List<Avaliacao> findByFilme(Filme filme);

    // Buscar avaliações de um usuário específico
    List<Avaliacao> findByUsuario(Usuario usuario);

    // Buscar avaliação específica de um usuário para um filme
    Optional<Avaliacao> findByUsuarioAndFilme(Usuario usuario, Filme filme);

    // Verificar se usuário já avaliou um filme
    boolean existsByUsuarioAndFilme(Usuario usuario, Filme filme);

    // Buscar avaliações por nota
    List<Avaliacao> findByNota(Integer nota);

    // Buscar avaliações de um filme ordenadas por data (mais recentes primeiro)
    List<Avaliacao> findByFilmeOrderByDataCriacaoDesc(Filme filme);

    // Calcular média de notas de um filme
    @Query("SELECT AVG(a.nota) FROM Avaliacao a WHERE a.filme = :filme")
    Double calcularMediaNotasFilme(@Param("filme") Filme filme);

    // Contar total de avaliações de um filme
    Long countByFilme(Filme filme);

    // Buscar avaliações com comentários não nulos
    @Query("SELECT a FROM Avaliacao a WHERE a.comentario IS NOT NULL AND a.comentario != ''")
    List<Avaliacao> findAvaliacoesComComentarios();

    // Buscar avaliações de um filme com comentários
    @Query("SELECT a FROM Avaliacao a WHERE a.filme = :filme AND a.comentario IS NOT NULL AND a.comentario != ''")
    List<Avaliacao> findAvaliacoesComComentariosByFilme(@Param("filme") Filme filme);

    // Estatísticas de notas por filme
    @Query("SELECT a.nota, COUNT(a) FROM Avaliacao a WHERE a.filme = :filme GROUP BY a.nota ORDER BY a.nota")
    List<Object[]> getEstatisticasNotasByFilme(@Param("filme") Filme filme);
}