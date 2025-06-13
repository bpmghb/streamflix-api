package com.streamflix.api.controller;

import com.streamflix.api.dto.*;
import com.streamflix.api.entity.ListaFavoritos;
import com.streamflix.api.entity.ListaFavoritosFilmes;
import com.streamflix.api.entity.Filme;
import com.streamflix.api.entity.Usuario;
import com.streamflix.api.mapper.ListaFavoritosMapper;
import com.streamflix.api.mapper.CommonMapper;
import com.streamflix.api.service.ListaFavoritosService;
import com.streamflix.api.service.ListaFavoritosFilmesService;
import com.streamflix.api.service.FilmeService;
import com.streamflix.api.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/listas-favoritos")
@CrossOrigin(origins = "*")
public class ListaFavoritosController {

    @Autowired
    private ListaFavoritosService listaFavoritosService;

    @Autowired
    private ListaFavoritosFilmesService listaFavoritosFilmesService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private FilmeService filmeService;

    @Autowired
    private ListaFavoritosMapper listaFavoritosMapper;

    @Autowired
    private CommonMapper commonMapper;

    // ========== CRUD OPERATIONS ==========

    /**
     * CRUD - GET ALL: Listar listas públicas
     */
    @GetMapping("/publicas")
    public ResponseEntity<ApiResponseDto<List<ListaFavoritosResumoDto>>> listarListasPublicas() {
        List<ListaFavoritos> listas = listaFavoritosService.getListasPublicasOrdenadas();
        List<ListaFavoritosResumoDto> listasDto = listaFavoritosMapper.toResumoDtoListSimples(listas);

        return ResponseEntity.ok(commonMapper.toSuccessResponse(listasDto));
    }

    /**
     * CRUD - GET ONE: Obter lista por ID (verificando permissão de acesso)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<ListaFavoritosComFilmesDto>> obterListaPorId(
            @PathVariable Long id,
            Authentication authentication) {

        Optional<Usuario> usuarioOpt = usuarioService.getByLoginAndAtivo(authentication.getName());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(commonMapper.toErrorResponse("Usuário não autenticado"));
        }

        try {
            ListaFavoritos lista = listaFavoritosService.validarAcesso(id, usuarioOpt.get());
            List<ListaFavoritosFilmes> filmesNaLista = listaFavoritosFilmesService.getFilmesDaListaOrdenados(lista);

            ListaFavoritosComFilmesDto listaDto = listaFavoritosMapper.toComFilmesDto(lista, filmesNaLista);

            return ResponseEntity.ok(commonMapper.toSuccessResponse(listaDto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(commonMapper.toErrorResponse(e.getMessage()));
        }
    }

    /**
     * CRUD - CREATE: Criar nova lista de favoritos
     */
    @PostMapping
    public ResponseEntity<ApiResponseDto<ListaFavoritosResponseDto>> criarLista(
            @Valid @RequestBody ListaFavoritosCreateDto listaCreateDto,
            Authentication authentication) {

        Optional<Usuario> usuarioOpt = usuarioService.getByLoginAndAtivo(authentication.getName());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(commonMapper.toErrorResponse("Usuário não autenticado"));
        }

        try {
            ListaFavoritos lista = listaFavoritosMapper.toEntity(listaCreateDto, usuarioOpt.get());
            ListaFavoritos listaCriada = listaFavoritosService.create(lista);

            ListaFavoritosResponseDto listaDto = listaFavoritosMapper.toResponseDto(listaCriada, 0L);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(commonMapper.toSuccessResponse("Lista criada com sucesso", listaDto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(commonMapper.toErrorResponse(e.getMessage()));
        }
    }

    /**
     * CRUD - UPDATE: Atualizar lista (apenas proprietário) - CORRIGIDO
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<ListaFavoritosResponseDto>> atualizarLista(
            @PathVariable Long id,
            @Valid @RequestBody ListaFavoritosUpdateDto listaUpdateDto,
            Authentication authentication) {

        Optional<Usuario> usuarioOpt = usuarioService.getByLoginAndAtivo(authentication.getName());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(commonMapper.toErrorResponse("Usuário não autenticado"));
        }

        try {
            // Validar permissão de edição e obter lista existente
            ListaFavoritos listaExistente = listaFavoritosService.validarPermissaoEdicao(id, usuarioOpt.get());

            // Aplicar mudanças do DTO na entidade existente
            listaFavoritosMapper.updateEntity(listaExistente, listaUpdateDto);

            // Salvar as mudanças
            ListaFavoritos listaAtualizada = listaFavoritosService.update(id, listaExistente);
            Long totalFilmes = listaFavoritosFilmesService.contarFilmesNaLista(listaAtualizada);

            ListaFavoritosResponseDto listaDto = listaFavoritosMapper.toResponseDto(listaAtualizada, totalFilmes);

            return ResponseEntity.ok(commonMapper.toSuccessResponse("Lista atualizada com sucesso", listaDto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(commonMapper.toErrorResponse(e.getMessage()));
        }
    }

    /**
     * CRUD - DELETE: Deletar lista (apenas proprietário)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<String>> deletarLista(
            @PathVariable Long id,
            Authentication authentication) {

        Optional<Usuario> usuarioOpt = usuarioService.getByLoginAndAtivo(authentication.getName());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(commonMapper.toErrorResponse("Usuário não autenticado"));
        }

        try {
            // Validar permissão de edição
            listaFavoritosService.validarPermissaoEdicao(id, usuarioOpt.get());

            listaFavoritosService.delete(id);
            return ResponseEntity.ok(commonMapper.toSuccessResponse("Lista deletada com sucesso", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(commonMapper.toErrorResponse(e.getMessage()));
        }
    }

    // ========== GERENCIAMENTO DE FILMES NAS LISTAS ==========

    /**
     * Adicionar filme à lista
     */
    @PostMapping("/{listaId}/filmes")
    public ResponseEntity<ApiResponseDto<String>> adicionarFilmeNaLista(
            @PathVariable Long listaId,
            @Valid @RequestBody AdicionarFilmeListaDto adicionarFilmeDto,
            Authentication authentication) {

        Optional<Usuario> usuarioOpt = usuarioService.getByLoginAndAtivo(authentication.getName());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(commonMapper.toErrorResponse("Usuário não autenticado"));
        }

        try {
            // Validar permissão de edição
            ListaFavoritos lista = listaFavoritosService.validarPermissaoEdicao(listaId, usuarioOpt.get());

            // Buscar filme
            Optional<Filme> filmeOpt = filmeService.getOneAtivo(adicionarFilmeDto.filmeId());
            if (filmeOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(commonMapper.toErrorResponse("Filme não encontrado"));
            }

            listaFavoritosFilmesService.adicionarFilmeNaLista(lista, filmeOpt.get());

            return ResponseEntity.ok(commonMapper.toSuccessResponse("Filme adicionado à lista com sucesso", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(commonMapper.toErrorResponse(e.getMessage()));
        }
    }

    /**
     * Remover filme da lista
     */
    @DeleteMapping("/{listaId}/filmes/{filmeId}")
    public ResponseEntity<ApiResponseDto<String>> removerFilmeDaLista(
            @PathVariable Long listaId,
            @PathVariable Long filmeId,
            Authentication authentication) {

        Optional<Usuario> usuarioOpt = usuarioService.getByLoginAndAtivo(authentication.getName());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(commonMapper.toErrorResponse("Usuário não autenticado"));
        }

        try {
            // Validar permissão de edição
            ListaFavoritos lista = listaFavoritosService.validarPermissaoEdicao(listaId, usuarioOpt.get());

            // Buscar filme
            Optional<Filme> filmeOpt = filmeService.getOne(filmeId);
            if (filmeOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(commonMapper.toErrorResponse("Filme não encontrado"));
            }

            listaFavoritosFilmesService.removerFilmeDaLista(lista, filmeOpt.get());

            return ResponseEntity.ok(commonMapper.toSuccessResponse("Filme removido da lista com sucesso", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(commonMapper.toErrorResponse(e.getMessage()));
        }
    }

    /**
     * Toggle filme na lista (adicionar se não existe, remover se existe)
     */
    @PostMapping("/{listaId}/filmes/{filmeId}/toggle")
    public ResponseEntity<ApiResponseDto<String>> toggleFilmeNaLista(
            @PathVariable Long listaId,
            @PathVariable Long filmeId,
            Authentication authentication) {

        Optional<Usuario> usuarioOpt = usuarioService.getByLoginAndAtivo(authentication.getName());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(commonMapper.toErrorResponse("Usuário não autenticado"));
        }

        try {
            // Validar permissão de edição
            ListaFavoritos lista = listaFavoritosService.validarPermissaoEdicao(listaId, usuarioOpt.get());

            // Buscar filme
            Optional<Filme> filmeOpt = filmeService.getOneAtivo(filmeId);
            if (filmeOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(commonMapper.toErrorResponse("Filme não encontrado"));
            }

            boolean adicionado = listaFavoritosFilmesService.toggleFilmeNaLista(lista, filmeOpt.get());

            String message = adicionado ? "Filme adicionado à lista" : "Filme removido da lista";
            return ResponseEntity.ok(commonMapper.toSuccessResponse(message, null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(commonMapper.toErrorResponse(e.getMessage()));
        }
    }

    // ========== ENDPOINTS ESPECÍFICOS ==========

    /**
     * Listar minhas listas
     */
    @GetMapping("/minhas")
    public ResponseEntity<ApiResponseDto<List<ListaFavoritosResponseDto>>> listarMinhasListas(
            Authentication authentication) {

        Optional<Usuario> usuarioOpt = usuarioService.getByLoginAndAtivo(authentication.getName());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(commonMapper.toErrorResponse("Usuário não autenticado"));
        }

        List<ListaFavoritos> listas = listaFavoritosService.getListasByUsuarioOrdenadas(usuarioOpt.get());
        List<ListaFavoritosResponseDto> listasDto = listaFavoritosMapper.toResponseDtoListSimples(listas);

        return ResponseEntity.ok(commonMapper.toSuccessResponse(listasDto));
    }

    /**
     * Buscar listas públicas por nome
     */
    @GetMapping("/publicas/buscar")
    public ResponseEntity<ApiResponseDto<List<ListaFavoritosResumoDto>>> buscarListasPublicas(
            @RequestParam String nome) {

        List<ListaFavoritos> listas = listaFavoritosService.getListasPublicasByNome(nome);
        List<ListaFavoritosResumoDto> listasDto = listaFavoritosMapper.toResumoDtoListSimples(listas);

        return ResponseEntity.ok(commonMapper.toSuccessResponse(listasDto));
    }

    /**
     * Listar listas públicas de outros usuários (explorar)
     */
    @GetMapping("/explorar")
    public ResponseEntity<ApiResponseDto<List<ListaFavoritosResumoDto>>> explorarListas(
            Authentication authentication) {

        Optional<Usuario> usuarioOpt = usuarioService.getByLoginAndAtivo(authentication.getName());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(commonMapper.toErrorResponse("Usuário não autenticado"));
        }

        List<ListaFavoritos> listas = listaFavoritosService.getListasPublicasDeOutrosUsuarios(usuarioOpt.get());
        List<ListaFavoritosResumoDto> listasDto = listaFavoritosMapper.toResumoDtoListSimples(listas);

        return ResponseEntity.ok(commonMapper.toSuccessResponse(listasDto));
    }

    /**
     * Tornar lista pública
     */
    @PatchMapping("/{id}/tornar-publica")
    public ResponseEntity<ApiResponseDto<ListaFavoritosResponseDto>> tornarListaPublica(
            @PathVariable Long id,
            Authentication authentication) {

        Optional<Usuario> usuarioOpt = usuarioService.getByLoginAndAtivo(authentication.getName());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(commonMapper.toErrorResponse("Usuário não autenticado"));
        }

        try {
            // Validar permissão de edição
            listaFavoritosService.validarPermissaoEdicao(id, usuarioOpt.get());

            ListaFavoritos lista = listaFavoritosService.tornarPublica(id);
            Long totalFilmes = listaFavoritosFilmesService.contarFilmesNaLista(lista);

            ListaFavoritosResponseDto listaDto = listaFavoritosMapper.toResponseDto(lista, totalFilmes);

            return ResponseEntity.ok(commonMapper.toSuccessResponse("Lista tornada pública", listaDto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(commonMapper.toErrorResponse(e.getMessage()));
        }
    }

    /**
     * Tornar lista privada
     */
    @PatchMapping("/{id}/tornar-privada")
    public ResponseEntity<ApiResponseDto<ListaFavoritosResponseDto>> tornarListaPrivada(
            @PathVariable Long id,
            Authentication authentication) {

        Optional<Usuario> usuarioOpt = usuarioService.getByLoginAndAtivo(authentication.getName());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(commonMapper.toErrorResponse("Usuário não autenticado"));
        }

        try {
            // Validar permissão de edição
            listaFavoritosService.validarPermissaoEdicao(id, usuarioOpt.get());

            ListaFavoritos lista = listaFavoritosService.tornarPrivada(id);
            Long totalFilmes = listaFavoritosFilmesService.contarFilmesNaLista(lista);

            ListaFavoritosResponseDto listaDto = listaFavoritosMapper.toResponseDto(lista, totalFilmes);

            return ResponseEntity.ok(commonMapper.toSuccessResponse("Lista tornada privada", listaDto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(commonMapper.toErrorResponse(e.getMessage()));
        }
    }

    /**
     * Copiar lista de outro usuário (criar uma cópia)
     */
    @PostMapping("/{id}/copiar")
    public ResponseEntity<ApiResponseDto<ListaFavoritosResponseDto>> copiarLista(
            @PathVariable Long id,
            @RequestParam(required = false) String novoNome,
            Authentication authentication) {

        Optional<Usuario> usuarioOpt = usuarioService.getByLoginAndAtivo(authentication.getName());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(commonMapper.toErrorResponse("Usuário não autenticado"));
        }

        try {
            // Verificar se pode acessar a lista original
            ListaFavoritos listaOriginal = listaFavoritosService.validarAcesso(id, usuarioOpt.get());

            // Criar nova lista
            String nomeNovaLista = novoNome != null ? novoNome : "Cópia de " + listaOriginal.getNome();
            ListaFavoritos novaLista = listaFavoritosService.create(
                    nomeNovaLista,
                    listaOriginal.getDescricao(),
                    false, // Inicialmente privada
                    usuarioOpt.get()
            );

            // Copiar filmes
            listaFavoritosFilmesService.copiarFilmesEntrelistas(listaOriginal, novaLista);

            Long totalFilmes = listaFavoritosFilmesService.contarFilmesNaLista(novaLista);
            ListaFavoritosResponseDto listaDto = listaFavoritosMapper.toResponseDto(novaLista, totalFilmes);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(commonMapper.toSuccessResponse("Lista copiada com sucesso", listaDto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(commonMapper.toErrorResponse(e.getMessage()));
        }
    }

    /**
     * Limpar todos os filmes da lista
     */
    @DeleteMapping("/{id}/limpar")
    public ResponseEntity<ApiResponseDto<String>> limparLista(
            @PathVariable Long id,
            Authentication authentication) {

        Optional<Usuario> usuarioOpt = usuarioService.getByLoginAndAtivo(authentication.getName());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(commonMapper.toErrorResponse("Usuário não autenticado"));
        }

        try {
            // Validar permissão de edição
            ListaFavoritos lista = listaFavoritosService.validarPermissaoEdicao(id, usuarioOpt.get());

            listaFavoritosFilmesService.limparLista(lista);

            return ResponseEntity.ok(commonMapper.toSuccessResponse("Lista limpa com sucesso", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(commonMapper.toErrorResponse(e.getMessage()));
        }
    }

    /**
     * Obter estatísticas da lista
     */
    @GetMapping("/{id}/estatisticas")
    public ResponseEntity<ApiResponseDto<Object>> obterEstatisticasLista(
            @PathVariable Long id,
            Authentication authentication) {

        Optional<Usuario> usuarioOpt = usuarioService.getByLoginAndAtivo(authentication.getName());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(commonMapper.toErrorResponse("Usuário não autenticado"));
        }

        try {
            ListaFavoritos lista = listaFavoritosService.validarAcesso(id, usuarioOpt.get());
            Long totalFilmes = listaFavoritosFilmesService.contarFilmesNaLista(lista);

            var estatisticas = new java.util.HashMap<String, Object>();
            estatisticas.put("totalFilmes", totalFilmes);
            estatisticas.put("listaId", lista.getId());
            estatisticas.put("nomeLista", lista.getNome());
            estatisticas.put("publica", lista.getPublica());
            estatisticas.put("proprietario", lista.getUsuario().getNomeUsuario());

            return ResponseEntity.ok(commonMapper.toSuccessResponse(estatisticas));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(commonMapper.toErrorResponse(e.getMessage()));
        }
    }
}