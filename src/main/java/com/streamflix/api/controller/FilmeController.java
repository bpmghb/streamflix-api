package com.streamflix.api.controller;

import com.streamflix.api.dto.*;
import com.streamflix.api.entity.Filme;
import com.streamflix.api.entity.Usuario;
import com.streamflix.api.mapper.FilmeMapper;
import com.streamflix.api.mapper.CommonMapper;
import com.streamflix.api.service.FilmeService;
import com.streamflix.api.service.UsuarioService;
import com.streamflix.api.service.AvaliacaoService;
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
@RequestMapping("/api/filmes")
@CrossOrigin(origins = "*")
public class FilmeController {

    @Autowired
    private FilmeService filmeService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AvaliacaoService avaliacaoService;

    @Autowired
    private FilmeMapper filmeMapper;

    @Autowired
    private CommonMapper commonMapper;

    // ========== ENDPOINTS PARA USUÁRIOS COMUNS ==========

    /**
     * REQUISITO OBRIGATÓRIO: GET ALL com filtro de ordenação por popularidade
     * Listar filmes ativos (usuários comuns)
     */
    @GetMapping("/ativos")
    public ResponseEntity<ApiResponseDto<List<FilmeResumoDto>>> listarFilmesAtivos(
            @RequestParam(defaultValue = "false") boolean orderByPopularidade) {

        List<Filme> filmes = filmeService.getAll(orderByPopularidade);
        List<FilmeResumoDto> filmesDto = filmeMapper.toResumoDtoList(filmes);

        String message = orderByPopularidade ?
                "Filmes listados por popularidade" :
                "Filmes ativos listados";

        return ResponseEntity.ok(commonMapper.toSuccessResponse(message, filmesDto));
    }

    /**
     * REQUISITO OBRIGATÓRIO: GET ONE que incrementa contador de acessos
     * Buscar detalhes de filme específico (incrementa popularidade)
     */
    @GetMapping("/{id}/detalhes")
    public ResponseEntity<ApiResponseDto<FilmeDetalhesDto>> obterDetalhesFilme(@PathVariable Long id) {
        Optional<Filme> filmeOpt = filmeService.getOneAtivo(id); // INCREMENTA CONTADOR AUTOMATICAMENTE

        if (filmeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(commonMapper.toErrorResponse("Filme não encontrado"));
        }

        Filme filme = filmeOpt.get();

        // Buscar estatísticas de avaliações
        Double mediaAvaliacoes = avaliacaoService.calcularMediaNotasFilme(filme);
        Long totalAvaliacoes = avaliacaoService.contarAvaliacoesFilme(filme);

        FilmeDetalhesDto filmeDto = filmeMapper.toDetalhesDto(filme, mediaAvaliacoes, totalAvaliacoes);

        return ResponseEntity.ok(commonMapper.toSuccessResponse(filmeDto));
    }

    /**
     * REQUISITO OBRIGATÓRIO: Ranking de popularidade (Top N filmes mais populares)
     */
    @GetMapping("/ranking/popularidade")
    public ResponseEntity<ApiResponseDto<List<FilmeRankingDto>>> rankingPopularidade(
            @RequestParam(defaultValue = "10") int limit) {

        List<Filme> filmesPopulares = filmeService.getTopFilmesByPopularidade(limit);
        List<FilmeRankingDto> rankingDto = filmeMapper.toRankingDtoList(filmesPopulares);

        return ResponseEntity.ok(commonMapper.toSuccessResponse(
                "Top " + limit + " filmes mais populares",
                rankingDto
        ));
    }

    /**
     * Buscar filmes com filtros (com opção de ordenar por popularidade)
     */
    @GetMapping("/buscar")
    public ResponseEntity<ApiResponseDto<List<FilmeResumoDto>>> buscarFilmes(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String genero,
            @RequestParam(required = false) Integer anoLancamento,
            @RequestParam(required = false) String diretor,
            @RequestParam(defaultValue = "false") boolean orderByPopularidade) {

        List<Filme> filmes;

        if (titulo != null) {
            filmes = filmeService.getByTitulo(titulo);
        } else if (genero != null) {
            filmes = filmeService.getByGenero(genero);
        } else if (anoLancamento != null) {
            filmes = filmeService.getByAno(anoLancamento);
        } else if (diretor != null) {
            filmes = filmeService.getByDiretor(diretor);
        } else if (orderByPopularidade) {
            filmes = filmeService.getAllAtivosByPopularidade();
        } else {
            filmes = filmeService.getAllAtivos();
        }

        List<FilmeResumoDto> filmesDto = filmeMapper.toResumoDtoList(filmes);

        return ResponseEntity.ok(commonMapper.toSuccessResponse(filmesDto));
    }

    // ========== ENDPOINTS PARA ADMINISTRADORES ==========

    /**
     * Listar todos os filmes (incluindo inativos) - ADMIN
     */
    @GetMapping("/admin/todos")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponseDto<List<FilmeResponseDto>>> listarTodosFilmes() {
        List<Filme> filmes = filmeService.getAll();
        List<FilmeResponseDto> filmesDto = filmeMapper.toResponseDtoList(filmes);

        return ResponseEntity.ok(commonMapper.toSuccessResponse(filmesDto));
    }

    /**
     * Buscar filme por ID (admin pode ver inativos)
     */
    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponseDto<FilmeResponseDto>> obterFilmeAdmin(@PathVariable Long id) {
        Optional<Filme> filmeOpt = filmeService.getOne(id); // INCREMENTA CONTADOR MESMO PARA ADMIN

        if (filmeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(commonMapper.toErrorResponse("Filme não encontrado"));
        }

        FilmeResponseDto filmeDto = filmeMapper.toResponseDto(filmeOpt.get());
        return ResponseEntity.ok(commonMapper.toSuccessResponse(filmeDto));
    }

    /**
     * CRUD - CREATE: Criar novo filme (apenas admin)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponseDto<FilmeResponseDto>> criarFilme(
            @Valid @RequestBody FilmeCreateDto filmeCreateDto,
            Authentication authentication) {

        // Buscar usuário autenticado
        Optional<Usuario> usuarioOpt = usuarioService.getByLoginAndAtivo(authentication.getName());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(commonMapper.toErrorResponse("Usuário não autenticado"));
        }

        Filme filme = filmeMapper.toEntity(filmeCreateDto);
        Filme filmeCriado = filmeService.create(filme, usuarioOpt.get());
        FilmeResponseDto filmeDto = filmeMapper.toResponseDto(filmeCriado);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commonMapper.toSuccessResponse("Filme criado com sucesso", filmeDto));
    }

    /**
     * CRUD - UPDATE: Atualizar filme (apenas admin) - CORRIGIDO
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponseDto<FilmeResponseDto>> atualizarFilme(
            @PathVariable Long id,
            @Valid @RequestBody FilmeUpdateDto filmeUpdateDto) {

        try {
            // Buscar filme existente
            Filme filmeExistente = filmeService.getOne(id)
                    .orElseThrow(() -> new RuntimeException("Filme não encontrado com ID: " + id));

            // Aplicar mudanças do DTO na entidade
            filmeMapper.updateEntity(filmeExistente, filmeUpdateDto);

            // Salvar as mudanças
            Filme filmeAtualizado = filmeService.update(id, filmeExistente);
            FilmeResponseDto filmeDto = filmeMapper.toResponseDto(filmeAtualizado);

            return ResponseEntity.ok(commonMapper.toSuccessResponse("Filme atualizado com sucesso", filmeDto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(commonMapper.toErrorResponse(e.getMessage()));
        }
    }

    /**
     * CRUD - DELETE: Desativar filme (soft delete)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponseDto<String>> desativarFilme(@PathVariable Long id) {
        try {
            filmeService.delete(id);
            return ResponseEntity.ok(commonMapper.toSuccessResponse("Filme desativado com sucesso", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(commonMapper.toErrorResponse(e.getMessage()));
        }
    }

    /**
     * Ativar filme
     */
    @PatchMapping("/{id}/ativar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponseDto<FilmeResponseDto>> ativarFilme(@PathVariable Long id) {
        try {
            Filme filme = filmeService.ativar(id);
            FilmeResponseDto filmeDto = filmeMapper.toResponseDto(filme);

            return ResponseEntity.ok(commonMapper.toSuccessResponse("Filme ativado com sucesso", filmeDto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(commonMapper.toErrorResponse(e.getMessage()));
        }
    }

    /**
     * Reset contador de acessos (admin)
     */
    @PatchMapping("/{id}/reset-contador")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponseDto<FilmeResponseDto>> resetarContador(@PathVariable Long id) {
        try {
            Filme filme = filmeService.resetarContadorAcessos(id);
            FilmeResponseDto filmeDto = filmeMapper.toResponseDto(filme);

            return ResponseEntity.ok(commonMapper.toSuccessResponse("Contador de acessos resetado", filmeDto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(commonMapper.toErrorResponse(e.getMessage()));
        }
    }

    /**
     * Estatísticas por gênero (admin)
     */
    @GetMapping("/admin/estatisticas/generos")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponseDto<List<GeneroRankingDto>>> estatisticasGeneros() {
        List<Object[]> estatisticas = filmeService.getEstatisticasPorGenero();
        List<GeneroRankingDto> generoRanking = commonMapper.toGeneroRankingDtoList(estatisticas);

        return ResponseEntity.ok(commonMapper.toSuccessResponse(generoRanking));
    }
}