package com.streamflix.api.repository;

import com.streamflix.api.entity.ListaFavoritos;
import com.streamflix.api.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListaFavoritosRepository extends JpaRepository<ListaFavoritos, Long> {

    // Buscar listas de um usuário específico
    List<ListaFavoritos> findByUsuario(Usuario usuario);

    // Buscar apenas listas públicas
    List<ListaFavoritos> findByPublicaTrue();

    // Buscar listas públicas de outros usuários (exceto o próprio)
    @Query("SELECT l FROM ListaFavoritos l WHERE l.publica = true AND l.usuario != :usuario")
    List<ListaFavoritos> findListasPublicasDeOutrosUsuarios(Usuario usuario);

    // Buscar listas por nome (case-insensitive)
    List<ListaFavoritos> findByNomeContainingIgnoreCase(String nome);

    // Buscar listas públicas por nome
    List<ListaFavoritos> findByNomeContainingIgnoreCaseAndPublicaTrue(String nome);

    // Contar quantas listas um usuário possui
    Long countByUsuario(Usuario usuario);

    // Buscar listas ordenadas por data de criação (mais recentes primeiro)
    List<ListaFavoritos> findByUsuarioOrderByDataCriacaoDesc(Usuario usuario);

    // Buscar listas públicas ordenadas por data de criação
    List<ListaFavoritos> findByPublicaTrueOrderByDataCriacaoDesc();
}