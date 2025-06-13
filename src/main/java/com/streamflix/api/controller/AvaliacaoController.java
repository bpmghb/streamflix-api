package com.streamflix.api.controller;

import com.streamflix.api.dto.*;
import com.streamflix.api.entity.Avaliacao;
import com.streamflix.api.entity.Filme;
import com.streamflix.api.entity.Usuario;
import com.streamflix.api.mapper.AvaliacaoMapper;
import com.streamflix.api.mapper.CommonMapper;
import com.streamflix.api.service.AvaliacaoService;
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
@RequestMapping("/api/avaliacoes")
@CrossOrigin(origins = "*")
public class AvaliacaoController {

    @Autowired
    private AvaliacaoService avaliacaoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private FilmeService filmeService;

    @Autowired
    private AvaliacaoMapper avaliacaoMapper;

    @Autowired
    private CommonMapper commonMapper;

    // ========== CRUD OPERATIONS ==========

    /**
     * CRUD - GET ALL: Listar todas as avaliações
     */
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<AvaliacaoResponseDto>>> listarTodasAvaliacoes() {
        List<Avaliacao> avaliacoes = avaliacaoService.getAll();
        List<AvaliacaoResponseDto> avaliacoesDto = avaliacaoMapper.toResponseDtoList(avaliacoes);

        return ResponseEntity.ok(commonMapper.toSuccessResponse(avaliacoesDto));
    }

    /**
     * CRUD - GET ONE: Buscar avaliação por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<AvaliacaoResponseDto>> obterAvaliacaoPorId(@PathVariable Long id) {
        Optional<Avaliacao> avaliacaoOpt = avaliacaoService.getOne(id);

        if (avaliacaoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(commonMapper.toErrorResponse("Avaliação não encontrada"));
        }

        AvaliacaoResponseDto avaliacaoDto = avaliacaoMapper.toResponseDto(avaliacaoOpt.get());
        return ResponseEntity.ok(commonMapper.toSuccessResponse(avaliacaoDto));
    }

    /**
     * CRUD - CREATE: Criar nova avaliação
     */
    @PostMapping
    public ResponseEntity<ApiResponseDto<AvaliacaoResponseDto>> criarAvaliacao(
            @Valid @RequestBody AvaliacaoCreateDto avaliacaoCreateDto,
            Authentication authentication) {

        // Buscar usuário autenticado
        Optional<Usuario> usuarioOpt = usuarioService.getByLoginAndAtivo(authentication.getName());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(commonMapper.toErrorResponse("Usuário não autenticado"));
        }

        // Buscar filme
        Optional<Filme> filmeOpt = filmeService.getOne(avaliacaoCreateDto.filmeId());
        if (filmeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(commonMapper.toErrorResponse("Filme não encontrado"));
        }

        try {
            Avaliacao avaliacao = avaliacaoMapper.toEntity(avaliacaoCreateDto, usuarioOpt.get(), filmeOpt.get());
            Avaliacao avaliacaoCriada = avaliacaoService.create(avaliacao);
            AvaliacaoResponseDto avaliacaoDto = avaliacaoMapper.toResponseDto(avaliacaoCriada);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(commonMapper.toSuccessResponse("Avaliação criada com sucesso", avaliacaoDto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(commonMapper.toErrorResponse(e.getMessage()));
        }
    }

    /**
     * CRUD - UPDATE: Atualizar avaliação existente - CORRIGIDO
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<AvaliacaoResponseDto>> atualizarAvaliacao(
            @PathVariable Long id,
            @Valid @RequestBody AvaliacaoUpdateDto avaliacaoUpdateDto,
            Authentication authentication) {

        // Verificar se avaliação existe
        Optional<Avaliacao> avaliacaoOpt = avaliacaoService.getOne(id);
        if (avaliacaoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(commonMapper.toErrorResponse("Avaliação não encontrada"));
        }

        // Verificar se usuário é o proprietário da avaliação
        Avaliacao avaliacaoExistente = avaliacaoOpt.get();
        if (!avaliacaoExistente.getUsuario().getNomeUsuario().equals(authentication.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(commonMapper.toErrorResponse("Você só pode editar suas próprias avaliações"));
        }

        try {
            // Aplicar mudanças do DTO na entidade existente
            avaliacaoMapper.updateEntity(avaliacaoExistente, avaliacaoUpdateDto);

            // Salvar as mudanças
            Avaliacao avaliacaoAtualizada = avaliacaoService.update(id, avaliacaoExistente);
            AvaliacaoResponseDto avaliacaoDto = avaliacaoMapper.toResponseDto(avaliacaoAtualizada);

            return ResponseEntity.ok(commonMapper.toSuccessResponse("Avaliação atualizada com sucesso", avaliacaoDto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(commonMapper.toErrorResponse(e.getMessage()));
        }
    }

    /**
     * CRUD - DELETE: Deletar avaliação
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<String>> deletarAvaliacao(
            @PathVariable Long id,
            Authentication authentication) {

        // Verificar se avaliação existe
        Optional<Avaliacao> avaliacaoOpt = avaliacaoService.getOne(id);
        if (avaliacaoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(commonMapper.toErrorResponse("Avaliação não encontrada"));
        }

        // Verificar se usuário é o proprietário da avaliação
        Avaliacao avaliacao = avaliacaoOpt.get();
        if (!avaliacao.getUsuario().getNomeUsuario().equals(authentication.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(commonMapper.toErrorResponse("Você só pode deletar suas próprias avaliações"));
        }

        try {
            avaliacaoService.delete(id);
            return ResponseEntity.ok(commonMapper.toSuccessResponse("Avaliação deletada com sucesso", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(commonMapper.toErrorResponse(e.getMessage()));
        }
    }

    // ========== ENDPOINTS ESPECÍFICOS ==========

    /**
     * Listar avaliações de um filme específico
     */
    @GetMapping("/filme/{filmeId}")
    public ResponseEntity<ApiResponseDto<List<AvaliacaoResumoDto>>> listarAvaliacoesPorFilme(@PathVariable Long filmeId) {
        Optional<Filme> filmeOpt = filmeService.getOne(filmeId);
        if (filmeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(commonMapper.toErrorResponse("Filme não encontrado"));
        }

        List<Avaliacao> avaliacoes = avaliacaoService.getAvaliacoesByFilmeOrdenadas(filmeOpt.get());
        List<AvaliacaoResumoDto> avaliacoesDto = avaliacaoMapper.toResumoDtoList(avaliacoes);

        return ResponseEntity.ok(commonMapper.toSuccessResponse(avaliacoesDto));
    }

    /**
     * Listar minhas avaliações (usuário autenticado)
     */
    @GetMapping("/minhas")
    public ResponseEntity<ApiResponseDto<List<AvaliacaoResponseDto>>> listarMinhasAvaliacoes(
            Authentication authentication) {

        Optional<Usuario> usuarioOpt = usuarioService.getByLoginAndAtivo(authentication.getName());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(commonMapper.toErrorResponse("Usuário não autenticado"));
        }

        List<Avaliacao> avaliacoes = avaliacaoService.getAvaliacoesByUsuario(usuarioOpt.get());
        List<AvaliacaoResponseDto> avaliacoesDto = avaliacaoMapper.toResponseDtoList(avaliacoes);

        return ResponseEntity.ok(commonMapper.toSuccessResponse(avaliacoesDto));
    }

    /**
     * Obter minha avaliação de um filme específico
     */
    @GetMapping("/filme/{filmeId}/minha")
    public ResponseEntity<ApiResponseDto<AvaliacaoResponseDto>> obterMinhaAvaliacaoDoFilme(
            @PathVariable Long filmeId,
            Authentication authentication) {

        Optional<Usuario> usuarioOpt = usuarioService.getByLoginAndAtivo(authentication.getName());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(commonMapper.toErrorResponse("Usuário não autenticado"));
        }

        Optional<Filme> filmeOpt = filmeService.getOne(filmeId);
        if (filmeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(commonMapper.toErrorResponse("Filme não encontrado"));
        }

        Optional<Avaliacao> avaliacaoOpt = avaliacaoService.getAvaliacaoByUsuarioAndFilme(
                usuarioOpt.get(), filmeOpt.get());

        if (avaliacaoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(commonMapper.toErrorResponse("Você ainda não avaliou este filme"));
        }

        AvaliacaoResponseDto avaliacaoDto = avaliacaoMapper.toResponseDto(avaliacaoOpt.get());
        return ResponseEntity.ok(commonMapper.toSuccessResponse(avaliacaoDto));
    }

    /**
     * Criar ou atualizar avaliação (upsert)
     */
    @PostMapping("/filme/{filmeId}/avaliar")
    public ResponseEntity<ApiResponseDto<AvaliacaoResponseDto>> avaliarFilme(
            @PathVariable Long filmeId,
            @Valid @RequestBody AvaliacaoUpdateDto avaliacaoDto,
            Authentication authentication) {

        Optional<Usuario> usuarioOpt = usuarioService.getByLoginAndAtivo(authentication.getName());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(commonMapper.toErrorResponse("Usuário não autenticado"));
        }

        Optional<Filme> filmeOpt = filmeService.getOne(filmeId);
        if (filmeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(commonMapper.toErrorResponse("Filme não encontrado"));
        }

        try {
            Avaliacao avaliacao = avaliacaoService.criarOuAtualizarAvaliacao(
                    avaliacaoDto.nota(),
                    avaliacaoDto.comentario(),
                    usuarioOpt.get(),
                    filmeOpt.get()
            );

            AvaliacaoResponseDto avaliacaoResponseDto = avaliacaoMapper.toResponseDto(avaliacao);

            return ResponseEntity.ok(commonMapper.toSuccessResponse(
                    "Avaliação salva com sucesso",
                    avaliacaoResponseDto
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(commonMapper.toErrorResponse(e.getMessage()));
        }
    }

    /**
     * Obter estatísticas de avaliações de um filme
     */
    @GetMapping("/filme/{filmeId}/estatisticas")
    public ResponseEntity<ApiResponseDto<AvaliacaoEstatisticasDto>> obterEstatisticasFilme(@PathVariable Long filmeId) {
        Optional<Filme> filmeOpt = filmeService.getOne(filmeId);
        if (filmeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(commonMapper.toErrorResponse("Filme não encontrado"));
        }

        Filme filme = filmeOpt.get();
        Double mediaNotas = avaliacaoService.calcularMediaNotasFilme(filme);
        Long totalAvaliacoes = avaliacaoService.contarAvaliacoesFilme(filme);
        List<Object[]> distribuicaoNotas = avaliacaoService.getEstatisticasNotasByFilme(filme);

        AvaliacaoEstatisticasDto estatisticas = avaliacaoMapper.processarEstatisticasRepository(
                filme, mediaNotas, totalAvaliacoes, distribuicaoNotas);

        return ResponseEntity.ok(commonMapper.toSuccessResponse(estatisticas));
    }

    /**
     * Listar avaliações com comentários
     */
    @GetMapping("/com-comentarios")
    public ResponseEntity<ApiResponseDto<List<AvaliacaoResumoDto>>> listarAvaliacoesComComentarios() {
        List<Avaliacao> avaliacoes = avaliacaoService.getAvaliacoesComComentarios();
        List<AvaliacaoResumoDto> avaliacoesDto = avaliacaoMapper.toResumoDtoList(avaliacoes);

        return ResponseEntity.ok(commonMapper.toSuccessResponse(avaliacoesDto));
    }

    /**
     * Listar avaliações de um filme que possuem comentários
     */
    @GetMapping("/filme/{filmeId}/com-comentarios")
    public ResponseEntity<ApiResponseDto<List<AvaliacaoResumoDto>>> listarAvaliacoesComComentariosPorFilme(
            @PathVariable Long filmeId) {

        Optional<Filme> filmeOpt = filmeService.getOne(filmeId);
        if (filmeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(commonMapper.toErrorResponse("Filme não encontrado"));
        }

        List<Avaliacao> avaliacoes = avaliacaoService.getAvaliacoesComComentariosByFilme(filmeOpt.get());
        List<AvaliacaoResumoDto> avaliacoesDto = avaliacaoMapper.toResumoDtoList(avaliacoes);

        return ResponseEntity.ok(commonMapper.toSuccessResponse(avaliacoesDto));
    }
}