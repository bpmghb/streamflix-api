package com.streamflix.api.mapper;

import com.streamflix.api.dto.*;
import com.streamflix.api.entity.Usuario;
import com.streamflix.api.entity.PerfilUsuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    /**
     * Converter UsuarioCreateDto para Usuario entity
     */
    public Usuario toEntity(UsuarioCreateDto dto) {
        Usuario usuario = new Usuario();
        usuario.setNomeUsuario(dto.nomeUsuario());
        usuario.setEmail(dto.email());
        usuario.setSenha(dto.senha());
        usuario.setPerfil(dto.perfil() != null ? dto.perfil() : PerfilUsuario.USUARIO);
        usuario.setAtivo(true);
        return usuario;
    }

    /**
     * Converter RegisterRequestDto para Usuario entity
     */
    public Usuario toEntity(RegisterRequestDto dto) {
        Usuario usuario = new Usuario();
        usuario.setNomeUsuario(dto.nomeUsuario());
        usuario.setEmail(dto.email());
        usuario.setSenha(dto.senha());
        usuario.setPerfil(PerfilUsuario.USUARIO); // Registro sempre cria usuário comum
        usuario.setAtivo(true);
        return usuario;
    }

    /**
     * Aplicar UsuarioUpdateDto em Usuario entity existente
     */
    public void updateEntity(Usuario usuario, UsuarioUpdateDto dto) {
        if (dto.nomeUsuario() != null) {
            usuario.setNomeUsuario(dto.nomeUsuario());
        }
        if (dto.email() != null) {
            usuario.setEmail(dto.email());
        }
        if (dto.perfil() != null) {
            usuario.setPerfil(dto.perfil());
        }
        if (dto.ativo() != null) {
            usuario.setAtivo(dto.ativo());
        }
        // Nota: senha é tratada separadamente no service para criptografia
    }

    /**
     * Converter Usuario entity para UsuarioResponseDto
     */
    public UsuarioResponseDto toResponseDto(Usuario usuario) {
        return new UsuarioResponseDto(
                usuario.getId(),
                usuario.getNomeUsuario(),
                usuario.getEmail(),
                usuario.getPerfil(),
                usuario.getAtivo(),
                usuario.getDataCriacao(),
                usuario.getDataAtualizacao()
        );
    }

    /**
     * Converter Usuario entity para UsuarioResumoDto
     */
    public UsuarioResumoDto toResumoDto(Usuario usuario) {
        return new UsuarioResumoDto(
                usuario.getId(),
                usuario.getNomeUsuario(),
                usuario.getPerfil()
        );
    }

    /**
     * Converter lista de Usuario entities para UsuarioResponseDto
     */
    public java.util.List<UsuarioResponseDto> toResponseDtoList(java.util.List<Usuario> usuarios) {
        return usuarios.stream()
                .map(this::toResponseDto)
                .toList();
    }

    /**
     * Converter lista de Usuario entities para UsuarioResumoDto
     */
    public java.util.List<UsuarioResumoDto> toResumoDtoList(java.util.List<Usuario> usuarios) {
        return usuarios.stream()
                .map(this::toResumoDto)
                .toList();
    }
}