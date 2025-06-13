package com.streamflix.api.repository;

import com.streamflix.api.entity.Usuario;
import com.streamflix.api.entity.PerfilUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Queries derivadas para busca
    Optional<Usuario> findByNomeUsuario(String nomeUsuario);

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByAtivo(Boolean ativo);

    List<Usuario> findByPerfil(PerfilUsuario perfil);

    boolean existsByNomeUsuario(String nomeUsuario);

    boolean existsByEmail(String email);

    // Query para autenticação (busca por nome de usuário OU email)
    @Query("SELECT u FROM Usuario u WHERE (u.nomeUsuario = :login OR u.email = :login) AND u.ativo = true")
    Optional<Usuario> findByLoginAndAtivo(String login);

    // Query para administradores ativos
    @Query("SELECT u FROM Usuario u WHERE u.perfil = 'ADMINISTRADOR' AND u.ativo = true")
    List<Usuario> findAdministradoresAtivos();
}