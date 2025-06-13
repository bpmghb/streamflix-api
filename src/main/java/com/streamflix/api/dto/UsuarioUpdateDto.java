package com.streamflix.api.dto;

import com.streamflix.api.entity.PerfilUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * DTO para atualizar usuário
 */
public record UsuarioUpdateDto(
        @Size(min = 3, max = 50, message = "Nome de usuário deve ter entre 3 e 50 caracteres")
        String nomeUsuario,

        @Email(message = "Email deve ser válido")
        String email,

        @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
        String senha,

        PerfilUsuario perfil,
        Boolean ativo
) {}

