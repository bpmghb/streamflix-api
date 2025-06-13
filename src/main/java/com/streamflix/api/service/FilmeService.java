package com.streamflix.api.service;

import com.streamflix.api.entity.Filme;
import com.streamflix.api.entity.Usuario;
import com.streamflix.api.repository.FilmeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FilmeService {

    @Autowired
    private FilmeRepository filmeRepository;

    // MÉTODOS CRUD OBRIGATÓRIOS

    /**
     * GET ONE - Buscar filme por ID (INCREMENTA CONTADOR DE ACESSOS - REQUISITO OBRIGATÓRIO)
     */
    public Optional<Filme> getOne(Long id) {
        Optional<Filme> filme = filmeRepository.findById(id);

        // REQUISITO OBRIGATÓRIO: Incrementar contador de acessos a cada GET
        if (filme.isPresent()) {
            Filme filmeEncontrado = filme.get();
            filmeEncontrado.incrementarAcessos(); // Método implementado na entidade
            filmeRepository.save(filmeEncontrado);
        }

        return filme;
    }

    /**
     * GET ONE para usuários comuns (apenas filmes ativos + incrementa acessos)
     */
    public Optional<Filme> getOneAtivo(Long id) {
        Optional<Filme> filme = filmeRepository.findByIdAndAtivoTrue(id);

        // REQUISITO OBRIGATÓRIO: Incrementar contador de acessos a cada GET
        if (filme.isPresent()) {
            Filme filmeEncontrado = filme.get();
            filmeEncontrado.incrementarAcessos();
            filmeRepository.save(filmeEncontrado);
        }

        return filme;
    }

    /**
     * GET ALL - Buscar todos os filmes (PERMITE ORDENAÇÃO POR POPULARIDADE - REQUISITO OBRIGATÓRIO)
     */
    public List<Filme> getAll() {
        return filmeRepository.findAll();
    }

    /**
     * GET ALL com filtro de ordenação por popularidade (REQUISITO OBRIGATÓRIO)
     */
    public List<Filme> getAll(boolean orderByPopularidade) {
        if (orderByPopularidade) {
            // REQUISITO OBRIGATÓRIO: Permitir ordenação por popularidade
            return filmeRepository.findByAtivoTrueOrderByContadorAcessosDesc();
        } else {
            return filmeRepository.findByAtivoTrue();
        }
    }

    /**
     * GET ALL apenas filmes ativos (para usuários comuns)
     */
    public List<Filme> getAllAtivos() {
        return filmeRepository.findByAtivoTrue();
    }

    /**
     * GET ALL filmes ativos ordenados por popularidade (REQUISITO OBRIGATÓRIO)
     */
    public List<Filme> getAllAtivosByPopularidade() {
        return filmeRepository.findByAtivoTrueOrderByContadorAcessosDesc();
    }

    /**
     * CREATE - Criar novo filme
     */
    public Filme create(Filme filme, Usuario criadoPor) {
        // Definir quem criou o filme
        filme.setCriadoPor(criadoPor);

        // Definir como ativo por padrão
        if (filme.getAtivo() == null) {
            filme.setAtivo(true);
        }

        // Inicializar contador de acessos
        if (filme.getContadorAcessos() == null) {
            filme.setContadorAcessos(0L);
        }

        return filmeRepository.save(filme);
    }

    /**
     * UPDATE - Atualizar filme existente
     */
    public Filme update(Long id, Filme filmeAtualizado) {
        Filme filmeExistente = filmeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Filme não encontrado com ID: " + id));

        // Atualizar campos (mantém contador de acessos e criador)
        filmeExistente.setTitulo(filmeAtualizado.getTitulo());
        filmeExistente.setDescricao(filmeAtualizado.getDescricao());
        filmeExistente.setGenero(filmeAtualizado.getGenero());
        filmeExistente.setAnoLancamento(filmeAtualizado.getAnoLancamento());
        filmeExistente.setDuracao(filmeAtualizado.getDuracao());
        filmeExistente.setDiretor(filmeAtualizado.getDiretor());
        filmeExistente.setUrlPoster(filmeAtualizado.getUrlPoster());

        if (filmeAtualizado.getAtivo() != null) {
            filmeExistente.setAtivo(filmeAtualizado.getAtivo());
        }

        return filmeRepository.save(filmeExistente);
    }

    /**
     * DELETE - Deletar filme (soft delete - marcar como inativo)
     */
    public void delete(Long id) {
        Filme filme = filmeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Filme não encontrado com ID: " + id));

        // Soft delete - apenas marcar como inativo
        filme.setAtivo(false);
        filmeRepository.save(filme);
    }

    /**
     * DELETE - Deletar filme permanentemente (hard delete)
     */
    public void deleteHard(Long id) {
        if (!filmeRepository.existsById(id)) {
            throw new RuntimeException("Filme não encontrado com ID: " + id);
        }
        filmeRepository.deleteById(id);
    }

    // MÉTODOS AUXILIARES E DE BUSCA

    /**
     * Buscar filmes por título
     */
    public List<Filme> getByTitulo(String titulo) {
        return filmeRepository.findByTituloContainingIgnoreCase(titulo);
    }

    /**
     * Buscar filmes por gênero
     */
    public List<Filme> getByGenero(String genero) {
        return filmeRepository.findByGeneroIgnoreCase(genero);
    }

    /**
     * Buscar filmes por ano
     */
    public List<Filme> getByAno(Integer ano) {
        return filmeRepository.findByAnoLancamento(ano);
    }

    /**
     * Buscar filmes por diretor
     */
    public List<Filme> getByDiretor(String diretor) {
        return filmeRepository.findByDiretorContainingIgnoreCase(diretor);
    }

    /**
     * Buscar filmes criados por um usuário
     */
    public List<Filme> getFilmesCriadosPor(Usuario usuario) {
        return filmeRepository.findByCriadoPor(usuario);
    }

    /**
     * REQUISITO OBRIGATÓRIO: Top N filmes mais populares
     */
    public List<Filme> getTopFilmesByPopularidade(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return filmeRepository.findTopFilmesByPopularidade(pageable);
    }

    /**
     * REQUISITO OBRIGATÓRIO: Buscar filmes com filtros ordenados por popularidade
     */
    public List<Filme> getFilmesComFiltrosByPopularidade(String genero, Integer ano) {
        return filmeRepository.findFilmesComFiltrosOrderByPopularidade(genero, ano);
    }

    /**
     * Ativar filme
     */
    public Filme ativar(Long id) {
        Filme filme = filmeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Filme não encontrado com ID: " + id));

        filme.setAtivo(true);
        return filmeRepository.save(filme);
    }

    /**
     * Desativar filme
     */
    public Filme desativar(Long id) {
        Filme filme = filmeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Filme não encontrado com ID: " + id));

        filme.setAtivo(false);
        return filmeRepository.save(filme);
    }

    /**
     * Resetar contador de acessos (para fins administrativos)
     */
    public Filme resetarContadorAcessos(Long id) {
        Filme filme = filmeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Filme não encontrado com ID: " + id));

        filme.setContadorAcessos(0L);
        return filmeRepository.save(filme);
    }

    /**
     * Obter estatísticas de gêneros
     */
    public List<Object[]> getEstatisticasPorGenero() {
        return filmeRepository.countFilmesByGenero();
    }
}