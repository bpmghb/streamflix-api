package com.streamflix.api.controller;

import com.streamflix.api.dto.*;
import com.streamflix.api.entity.Usuario;
import com.streamflix.api.entity.PerfilUsuario;
import com.streamflix.api.mapper.UsuarioMapper;
import com.streamflix.api.mapper.CommonMapper;
import com.streamflix.api.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private CommonMapper commonMapper;

    // ========== ENDPOINTS PARA USUÁRIOS AUTENTICADOS ==========

    /**
     * Obter perfil do usuário autenticado
     */
    @GetMapping("/perfil")
    public ResponseEntity<ApiResponseDto<UsuarioResponseDto>> obterPerfil(Authentication authentication) {
        Optional<Usuario> usuarioOpt = usuarioService.getByLoginAndAtivo(authentication.getName());

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(commonMapper.toErrorResponse("Usuário não encontrado"));
        }

        UsuarioResponseDto usuarioDto = usuarioMapper.toResponseDto(usuarioOpt.get());
        return ResponseEntity.ok(commonMapper.toSuccessResponse(usuarioDto));
    }

    /**
     * Atualizar perfil do usuário autenticado - CORRIGIDO
     */
    @PutMapping("/perfil")
    public ResponseEntity<ApiResponseDto<UsuarioResponseDto>> atualizarPerfil(
            @Valid @RequestBody UsuarioUpdateDto usuarioUpdateDto,
            Authentication authentication) {

        Optional<Usuario> usuarioOpt = usuarioService.getByLoginAndAtivo(authentication.getName());

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(commonMapper.toErrorResponse("Usuário não encontrado"));
        }

        try {
            // Usuários comuns não podem alterar o próprio perfil (role)
            UsuarioUpdateDto dtoSemPerfil = new UsuarioUpdateDto(
                    usuarioUpdateDto.nomeUsuario(),
                    usuarioUpdateDto.email(),
                    usuarioUpdateDto.senha(),
                    null, // Perfil não pode ser alterado pelo próprio usuário
                    null  // Ativo não pode ser alterado pelo próprio usuário
            );

            // Aplicar mudanças do DTO na entidade existente
            Usuario usuarioExistente = usuarioOpt.get();
            usuarioMapper.updateEntity(usuarioExistente, dtoSemPerfil);

            // Salvar as mudanças
            Usuario usuarioAtualizado = usuarioService.update(usuarioExistente.getId(), usuarioExistente);
            UsuarioResponseDto usuarioDto = usuarioMapper.toResponseDto(usuarioAtualizado);

            return ResponseEntity.ok(commonMapper.toSuccessResponse("Perfil atualizado com sucesso", usuarioDto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(commonMapper.toErrorResponse(e.getMessage()));
        }
    }

    /**
     * Desativar própria conta (soft delete)
     */
    @DeleteMapping("/perfil")
    public ResponseEntity<ApiResponseDto<String>> desativarConta(Authentication authentication) {
        Optional<Usuario> usuarioOpt = usuarioService.getByLoginAndAtivo(authentication.getName());

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(commonMapper.toErrorResponse("Usuário não encontrado"));
        }

        try {
            usuarioService.delete(usuarioOpt.get().getId());
            return ResponseEntity.ok(commonMapper.toSuccessResponse("Conta desativada com sucesso", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(commonMapper.toErrorResponse(e.getMessage()));
        }
    }

    // ========== ENDPOINTS PARA ADMINISTRADORES ==========

    /**
     * CRUD - GET ALL: Listar todos os usuários (admin)
     */
    @GetMapping("/admin/todos")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponseDto<List<UsuarioResponseDto>>> listarTodosUsuarios() {
        List<Usuario> usuarios = usuarioService.getAll();
        List<UsuarioResponseDto> usuariosDto = usuarioMapper.toResponseDtoList(usuarios);

        return ResponseEntity.ok(commonMapper.toSuccessResponse(usuariosDto));
    }

    /**
     * Listar apenas usuários ativos (admin)
     */
    @GetMapping("/admin/ativos")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponseDto<List<UsuarioResponseDto>>> listarUsuariosAtivos() {
        List<Usuario> usuarios = usuarioService.getUsuariosAtivos();
        List<UsuarioResponseDto> usuariosDto = usuarioMapper.toResponseDtoList(usuarios);

        return ResponseEntity.ok(commonMapper.toSuccessResponse(usuariosDto));
    }

    /**
     * CRUD - GET ONE: Buscar usuário por ID (admin)
     */
    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponseDto<UsuarioResponseDto>> obterUsuarioPorId(@PathVariable Long id) {
        Optional<Usuario> usuarioOpt = usuarioService.getOne(id);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(commonMapper.toErrorResponse("Usuário não encontrado"));
        }

        UsuarioResponseDto usuarioDto = usuarioMapper.toResponseDto(usuarioOpt.get());
        return ResponseEntity.ok(commonMapper.toSuccessResponse(usuarioDto));
    }

    /**
     * CRUD - CREATE: Criar usuário (admin)
     */
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponseDto<UsuarioResponseDto>> criarUsuario(
            @Valid @RequestBody UsuarioCreateDto usuarioCreateDto) {

        try {
            Usuario usuario = usuarioMapper.toEntity(usuarioCreateDto);
            Usuario usuarioCriado = usuarioService.create(usuario);
            UsuarioResponseDto usuarioDto = usuarioMapper.toResponseDto(usuarioCriado);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(commonMapper.toSuccessResponse("Usuário criado com sucesso", usuarioDto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(commonMapper.toErrorResponse(e.getMessage()));
        }
    }

    /**
     * CRUD - UPDATE: Atualizar usuário (admin) - CORRIGIDO
     */
    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponseDto<UsuarioResponseDto>> atualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioUpdateDto usuarioUpdateDto) {

        try {
            // Buscar usuário existente
            Usuario usuarioExistente = usuarioService.getOne(id)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));

            // Aplicar mudanças do DTO na entidade
            usuarioMapper.updateEntity(usuarioExistente, usuarioUpdateDto);

            // Salvar as mudanças
            Usuario usuarioAtualizado = usuarioService.update(id, usuarioExistente);
            UsuarioResponseDto usuarioDto = usuarioMapper.toResponseDto(usuarioAtualizado);

            return ResponseEntity.ok(commonMapper.toSuccessResponse("Usuário atualizado com sucesso", usuarioDto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(commonMapper.toErrorResponse(e.getMessage()));
        }
    }

    /**
     * CRUD - DELETE: Desativar usuário (admin)
     */
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponseDto<String>> desativarUsuario(@PathVariable Long id) {
        try {
            usuarioService.delete(id);
            return ResponseEntity.ok(commonMapper.toSuccessResponse("Usuário desativado com sucesso", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(commonMapper.toErrorResponse(e.getMessage()));
        }
    }

    /**
     * Ativar usuário (admin)
     */
    @PatchMapping("/admin/{id}/ativar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponseDto<UsuarioResponseDto>> ativarUsuario(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioService.ativar(id);
            UsuarioResponseDto usuarioDto = usuarioMapper.toResponseDto(usuario);

            return ResponseEntity.ok(commonMapper.toSuccessResponse("Usuário ativado com sucesso", usuarioDto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(commonMapper.toErrorResponse(e.getMessage()));
        }
    }

    /**
     * Promover usuário para administrador
     */
    @PatchMapping("/admin/{id}/promover")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponseDto<UsuarioResponseDto>> promoverParaAdmin(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioService.promoverParaAdmin(id);
            UsuarioResponseDto usuarioDto = usuarioMapper.toResponseDto(usuario);

            return ResponseEntity.ok(commonMapper.toSuccessResponse("Usuário promovido para administrador", usuarioDto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(commonMapper.toErrorResponse(e.getMessage()));
        }
    }

    /**
     * Rebaixar administrador para usuário comum
     */
    @PatchMapping("/admin/{id}/rebaixar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponseDto<UsuarioResponseDto>> rebaixarParaUsuario(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioService.rebaixarParaUsuario(id);
            UsuarioResponseDto usuarioDto = usuarioMapper.toResponseDto(usuario);

            return ResponseEntity.ok(commonMapper.toSuccessResponse("Usuário rebaixado para usuário comum", usuarioDto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(commonMapper.toErrorResponse(e.getMessage()));
        }
    }

    /**
     * Buscar usuários por perfil (admin)
     */
    @GetMapping("/admin/perfil/{perfil}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponseDto<List<UsuarioResponseDto>>> buscarPorPerfil(@PathVariable String perfil) {
        try {
            PerfilUsuario perfilEnum = PerfilUsuario.valueOf(perfil.toUpperCase());
            List<Usuario> usuarios = usuarioService.getUsuariosByPerfil(perfilEnum);
            List<UsuarioResponseDto> usuariosDto = usuarioMapper.toResponseDtoList(usuarios);

            return ResponseEntity.ok(commonMapper.toSuccessResponse(usuariosDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(commonMapper.toErrorResponse("Perfil inválido. Use: USUARIO ou ADMINISTRADOR"));
        }
    }

    /**
     * Buscar usuário por nome de usuário (admin)
     */
    @GetMapping("/admin/buscar/username/{nomeUsuario}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponseDto<UsuarioResponseDto>> buscarPorNomeUsuario(@PathVariable String nomeUsuario) {
        Optional<Usuario> usuarioOpt = usuarioService.getByNomeUsuario(nomeUsuario);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(commonMapper.toErrorResponse("Usuário não encontrado"));
        }

        UsuarioResponseDto usuarioDto = usuarioMapper.toResponseDto(usuarioOpt.get());
        return ResponseEntity.ok(commonMapper.toSuccessResponse(usuarioDto));
    }

    /**
     * Buscar usuário por email (admin)
     */
    @GetMapping("/admin/buscar/email/{email}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponseDto<UsuarioResponseDto>> buscarPorEmail(@PathVariable String email) {
        Optional<Usuario> usuarioOpt = usuarioService.getByEmail(email);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(commonMapper.toErrorResponse("Usuário não encontrado"));
        }

        UsuarioResponseDto usuarioDto = usuarioMapper.toResponseDto(usuarioOpt.get());
        return ResponseEntity.ok(commonMapper.toSuccessResponse(usuarioDto));
    }
}