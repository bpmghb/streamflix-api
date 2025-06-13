package com.streamflix.api.controller;

import com.streamflix.api.dto.*;
import com.streamflix.api.entity.Filme;
import com.streamflix.api.entity.Usuario;
import com.streamflix.api.mapper.CommonMapper;
import com.streamflix.api.mapper.FilmeMapper;
import com.streamflix.api.mapper.UsuarioMapper;
import com.streamflix.api.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private FilmeService filmeService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AvaliacaoService avaliacaoService;

    @Autowired
    private ListaFavoritosService listaFavoritosService;

    @Autowired
    private ListaFavoritosFilmesService listaFavoritosFilmesService;

    @Autowired
    private FilmeMapper filmeMapper;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private CommonMapper commonMapper;

    /**
     * Dashboard público com estatísticas básicas
     */
    @GetMapping("/publico")
    public ResponseEntity<ApiResponseDto<Object>> dashboardPublico() {
        // Estatísticas básicas que podem ser públicas
        List<Filme> filmesAtivos = filmeService.getAllAtivos();
        List<Filme> topFilmesPopulares = filmeService.getTopFilmesByPopularidade(5);
        List<Object[]> estatisticasGeneros = filmeService.getEstatisticasPorGenero();

        var dashboard = new java.util.HashMap<String, Object>();
        dashboard.put("totalFilmesAtivos", filmesAtivos.size());
        dashboard.put("topFilmesPopulares", filmeMapper.toRankingDtoList(topFilmesPopulares));
        dashboard.put("generos", commonMapper.toGeneroRankingDtoList(estatisticasGeneros));

        return ResponseEntity.ok(commonMapper.toSuccessResponse(dashboard));
    }

    /**
     * Dashboard administrativo completo (apenas admin)
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponseDto<DashboardAdminDto>> dashboardAdmin() {
        // Estatísticas gerais
        List<Usuario> todosUsuarios = usuarioService.getAll();
        List<Filme> todosFilmes = filmeService.getAll();
        List<Filme> filmesAtivos = filmeService.getAllAtivos();

        Long totalUsuarios = (long) todosUsuarios.size();
        Long totalFilmes = (long) todosFilmes.size();
        Long totalFilmesAtivos = (long) filmesAtivos.size();

        // Para simplificar, vamos criar estatísticas básicas
        // Em um cenário real, você criaria queries específicas no repository
        EstatisticasDto estatisticas = commonMapper.toEstatisticasDto(
                totalFilmes,
                totalUsuarios,
                0L, // totalAvaliacoes - seria calculado com query específica
                0L, // totalListasFavoritos - seria calculado com query específica
                0.0 // mediaGeralAvaliacoes - seria calculada com query específica
        );

        // Top filmes populares
        List<Filme> topFilmesPopulares = filmeService.getTopFilmesByPopularidade(10);
        List<FilmeRankingDto> rankingFilmes = filmeMapper.toRankingDtoList(topFilmesPopulares);

        // Ranking de gêneros
        List<Object[]> estatisticasGeneros = filmeService.getEstatisticasPorGenero();
        List<GeneroRankingDto> rankingGeneros = commonMapper.toGeneroRankingDtoList(estatisticasGeneros);

        // Usuários recentes (últimos 5 ativos)
        List<Usuario> usuariosAtivos = usuarioService.getUsuariosAtivos();
        List<UsuarioResumoDto> usuariosRecentes = usuarioMapper.toResumoDtoList(
                usuariosAtivos.stream().limit(5).toList()
        );

        DashboardAdminDto dashboard = commonMapper.toDashboardAdminDto(
                estatisticas,
                rankingFilmes,
                rankingGeneros,
                usuariosRecentes
        );

        return ResponseEntity.ok(commonMapper.toSuccessResponse(dashboard));
    }

    /**
     * Estatísticas de filmes mais populares
     */
    @GetMapping("/filmes/populares")
    public ResponseEntity<ApiResponseDto<List<FilmeRankingDto>>> filmesPopulares(
            @RequestParam(defaultValue = "10") int limit) {

        List<Filme> filmesPopulares = filmeService.getTopFilmesByPopularidade(limit);
        List<FilmeRankingDto> rankingDto = filmeMapper.toRankingDtoList(filmesPopulares);

        return ResponseEntity.ok(commonMapper.toSuccessResponse(rankingDto));
    }

    /**
     * Estatísticas por gênero
     */
    @GetMapping("/generos/estatisticas")
    public ResponseEntity<ApiResponseDto<List<GeneroRankingDto>>> estatisticasGeneros() {
        List<Object[]> estatisticas = filmeService.getEstatisticasPorGenero();
        List<GeneroRankingDto> generoRanking = commonMapper.toGeneroRankingDtoList(estatisticas);

        return ResponseEntity.ok(commonMapper.toSuccessResponse(generoRanking));
    }

    /**
     * Filmes mais adicionados às listas de favoritos - CORREÇÃO FINAL
     */
    @GetMapping("/filmes/mais-favoritos")
    public ResponseEntity<ApiResponseDto<List<java.util.Map<String, Object>>>> filmesMaisFavoritos() {
        List<Object[]> filmesMaisFavoritos = listaFavoritosFilmesService.getFilmesMaisAdicionadosListas();

        // Converter para formato mais amigável - TIPO CORRIGIDO
        List<java.util.Map<String, Object>> resultado = filmesMaisFavoritos.stream()
                .limit(10) // Top 10
                .map(row -> {
                    Filme filme = (Filme) row[0];
                    Long totalAdicoes = (Long) row[1];

                    // Usar Map genérico ao invés de HashMap específico
                    java.util.Map<String, Object> item = new java.util.HashMap<>();
                    item.put("filme", filmeMapper.toResumoDto(filme));
                    item.put("totalAdicoes", totalAdicoes);
                    return item;
                })
                .toList();

        return ResponseEntity.ok(commonMapper.toSuccessResponse(resultado));
    }

    /**
     * Estatísticas básicas do sistema
     */
    @GetMapping("/estatisticas")
    public ResponseEntity<ApiResponseDto<Object>> estatisticasBasicas() {
        var estatisticas = new java.util.HashMap<String, Object>();

        // Contadores básicos
        estatisticas.put("totalFilmesAtivos", filmeService.getAllAtivos().size());
        estatisticas.put("totalUsuariosAtivos", usuarioService.getUsuariosAtivos().size());

        // Filmes mais populares (top 3)
        List<Filme> topFilmes = filmeService.getTopFilmesByPopularidade(3);
        estatisticas.put("topFilmesPopulares", filmeMapper.toResumoDtoList(topFilmes));

        return ResponseEntity.ok(commonMapper.toSuccessResponse(estatisticas));
    }

    /**
     * Endpoint de health check
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponseDto<Object>> healthCheck() {
        var health = new java.util.HashMap<String, Object>();
        health.put("status", "UP");
        health.put("timestamp", java.time.LocalDateTime.now());
        health.put("version", "1.0.0");

        return ResponseEntity.ok(commonMapper.toSuccessResponse(health));
    }
}