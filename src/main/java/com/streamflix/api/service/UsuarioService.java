package com.streamflix.api.service;

import com.streamflix.api.entity.Usuario;
import com.streamflix.api.entity.PerfilUsuario;
import com.streamflix.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // MÉTODOS CRUD OBRIGATÓRIOS

    /**
     * GET ONE - Buscar usuário por ID
     */
    public Optional<Usuario> getOne(Long id) {
        return usuarioRepository.findById(id);
    }

    /**
     * GET ALL - Buscar todos os usuários
     */
    public List<Usuario> getAll() {
        return usuarioRepository.findAll();
    }

    /**
     * CREATE - Criar novo usuário
     */
    public Usuario create(Usuario usuario) {
        // Validar se nome de usuário já existe
        if (usuarioRepository.existsByNomeUsuario(usuario.getNomeUsuario())) {
            throw new RuntimeException("Nome de usuário já existe: " + usuario.getNomeUsuario());
        }

        // Validar se email já existe
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("Email já existe: " + usuario.getEmail());
        }

        // Criptografar senha
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        // Definir perfil padrão se não especificado
        if (usuario.getPerfil() == null) {
            usuario.setPerfil(PerfilUsuario.USUARIO);
        }

        // Definir como ativo por padrão
        if (usuario.getAtivo() == null) {
            usuario.setAtivo(true);
        }

        return usuarioRepository.save(usuario);
    }

    /**
     * UPDATE - Atualizar usuário existente
     */
    public Usuario update(Long id, Usuario usuarioAtualizado) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));

        // Validar se novo nome de usuário já existe (exceto para o próprio usuário)
        if (!usuarioExistente.getNomeUsuario().equals(usuarioAtualizado.getNomeUsuario()) &&
                usuarioRepository.existsByNomeUsuario(usuarioAtualizado.getNomeUsuario())) {
            throw new RuntimeException("Nome de usuário já existe: " + usuarioAtualizado.getNomeUsuario());
        }

        // Validar se novo email já existe (exceto para o próprio usuário)
        if (!usuarioExistente.getEmail().equals(usuarioAtualizado.getEmail()) &&
                usuarioRepository.existsByEmail(usuarioAtualizado.getEmail())) {
            throw new RuntimeException("Email já existe: " + usuarioAtualizado.getEmail());
        }

        // Atualizar campos
        usuarioExistente.setNomeUsuario(usuarioAtualizado.getNomeUsuario());
        usuarioExistente.setEmail(usuarioAtualizado.getEmail());

        // Só atualizar senha se uma nova foi fornecida
        if (usuarioAtualizado.getSenha() != null && !usuarioAtualizado.getSenha().isEmpty()) {
            usuarioExistente.setSenha(passwordEncoder.encode(usuarioAtualizado.getSenha()));
        }

        if (usuarioAtualizado.getPerfil() != null) {
            usuarioExistente.setPerfil(usuarioAtualizado.getPerfil());
        }

        if (usuarioAtualizado.getAtivo() != null) {
            usuarioExistente.setAtivo(usuarioAtualizado.getAtivo());
        }

        return usuarioRepository.save(usuarioExistente);
    }

    /**
     * DELETE - Deletar usuário (soft delete - marcar como inativo)
     */
    public void delete(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));

        // Soft delete - apenas marcar como inativo
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }

    /**
     * DELETE - Deletar usuário permanentemente (hard delete)
     */
    public void deleteHard(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado com ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    // MÉTODOS AUXILIARES

    /**
     * Buscar usuários ativos
     */
    public List<Usuario> getUsuariosAtivos() {
        return usuarioRepository.findByAtivo(true);
    }

    /**
     * Buscar usuários por perfil
     */
    public List<Usuario> getUsuariosByPerfil(PerfilUsuario perfil) {
        return usuarioRepository.findByPerfil(perfil);
    }

    /**
     * Buscar usuário por nome de usuário
     */
    public Optional<Usuario> getByNomeUsuario(String nomeUsuario) {
        return usuarioRepository.findByNomeUsuario(nomeUsuario);
    }

    /**
     * Buscar usuário por email
     */
    public Optional<Usuario> getByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    /**
     * Buscar usuário para autenticação (por nome de usuário OU email)
     */
    public Optional<Usuario> getByLoginAndAtivo(String login) {
        return usuarioRepository.findByLoginAndAtivo(login);
    }

    /**
     * Validar senha
     */
    public boolean validarSenha(String senhaRaw, String senhaEncriptada) {
        return passwordEncoder.matches(senhaRaw, senhaEncriptada);
    }

    /**
     * Verificar se usuário é administrador
     */
    public boolean isAdministrador(Usuario usuario) {
        return usuario.getPerfil() == PerfilUsuario.ADMINISTRADOR;
    }

    /**
     * Ativar usuário
     */
    public Usuario ativar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));

        usuario.setAtivo(true);
        return usuarioRepository.save(usuario);
    }

    /**
     * Desativar usuário
     */
    public Usuario desativar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));

        usuario.setAtivo(false);
        return usuarioRepository.save(usuario);
    }

    /**
     * Promover usuário para administrador
     */
    public Usuario promoverParaAdmin(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));

        usuario.setPerfil(PerfilUsuario.ADMINISTRADOR);
        return usuarioRepository.save(usuario);
    }

    /**
     * Rebaixar administrador para usuário comum
     */
    public Usuario rebaixarParaUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));

        usuario.setPerfil(PerfilUsuario.USUARIO);
        return usuarioRepository.save(usuario);
    }
}