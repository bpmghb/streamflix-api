package com.streamflix.api.service;

import com.streamflix.api.entity.Avaliacao;
import com.streamflix.api.entity.Filme;
import com.streamflix.api.entity.Usuario;
import com.streamflix.api.repository.AvaliacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AvaliacaoService {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    // MÉTODOS CRUD OBRIGATÓRIOS

    /**
     * GET ONE - Buscar avaliação por ID
     */
    public Optional<Avaliacao> getOne(Long id) {
        return avaliacaoRepository.findById(id);
    }

    /**
     * GET ALL - Buscar todas as avaliações
     */
    public List<Avaliacao> getAll() {
        return avaliacaoRepository.findAll();
    }

    /**
     * CREATE - Criar nova avaliação
     */
    public Avaliacao create(Avaliacao avaliacao) {
        // Verificar se usuário já avaliou este filme
        if (avaliacaoRepository.existsByUsuarioAndFilme(avaliacao.getUsuario(), avaliacao.getFilme())) {
            throw new RuntimeException("Usuário já avaliou este filme. Use o método update para alterar a avaliação.");
        }

        // Validar nota (entre 1 e 5)
        if (avaliacao.getNota() < 1 || avaliacao.getNota() > 5) {
            throw new RuntimeException("A nota deve ser entre 1 e 5");
        }

        return avaliacaoRepository.save(avaliacao);
    }

    /**
     * CREATE - Criar avaliação com parâmetros separados
     */
    public Avaliacao create(Integer nota, String comentario, Usuario usuario, Filme filme) {
        Avaliacao avaliacao = new Avaliacao(nota, comentario, usuario, filme);
        return create(avaliacao);
    }

    /**
     * UPDATE - Atualizar avaliação existente
     */
    public Avaliacao update(Long id, Avaliacao avaliacaoAtualizada) {
        Avaliacao avaliacaoExistente = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Avaliação não encontrada com ID: " + id));

        // Validar nota (entre 1 e 5)
        if (avaliacaoAtualizada.getNota() < 1 || avaliacaoAtualizada.getNota() > 5) {
            throw new RuntimeException("A nota deve ser entre 1 e 5");
        }

        // Atualizar campos (não permite alterar usuário ou filme)
        avaliacaoExistente.setNota(avaliacaoAtualizada.getNota());
        avaliacaoExistente.setComentario(avaliacaoAtualizada.getComentario());

        return avaliacaoRepository.save(avaliacaoExistente);
    }

    /**
     * DELETE - Deletar avaliação
     */
    public void delete(Long id) {
        if (!avaliacaoRepository.existsById(id)) {
            throw new RuntimeException("Avaliação não encontrada com ID: " + id);
        }
        avaliacaoRepository.deleteById(id);
    }

    // MÉTODOS AUXILIARES

    /**
     * Buscar avaliações de um filme específico
     */
    public List<Avaliacao> getAvaliacoesByFilme(Filme filme) {
        return avaliacaoRepository.findByFilme(filme);
    }

    /**
     * Buscar avaliações de um filme ordenadas por data (mais recentes primeiro)
     */
    public List<Avaliacao> getAvaliacoesByFilmeOrdenadas(Filme filme) {
        return avaliacaoRepository.findByFilmeOrderByDataCriacaoDesc(filme);
    }

    /**
     * Buscar avaliações de um usuário específico
     */
    public List<Avaliacao> getAvaliacoesByUsuario(Usuario usuario) {
        return avaliacaoRepository.findByUsuario(usuario);
    }

    /**
     * Buscar avaliação específica de um usuário para um filme
     */
    public Optional<Avaliacao> getAvaliacaoByUsuarioAndFilme(Usuario usuario, Filme filme) {
        return avaliacaoRepository.findByUsuarioAndFilme(usuario, filme);
    }

    /**
     * Verificar se usuário já avaliou um filme
     */
    public boolean usuarioJaAvaliouFilme(Usuario usuario, Filme filme) {
        return avaliacaoRepository.existsByUsuarioAndFilme(usuario, filme);
    }

    /**
     * Buscar avaliações por nota específica
     */
    public List<Avaliacao> getAvaliacoesByNota(Integer nota) {
        return avaliacaoRepository.findByNota(nota);
    }

    /**
     * Buscar avaliações que possuem comentários
     */
    public List<Avaliacao> getAvaliacoesComComentarios() {
        return avaliacaoRepository.findAvaliacoesComComentarios();
    }

    /**
     * Buscar avaliações com comentários de um filme específico
     */
    public List<Avaliacao> getAvaliacoesComComentariosByFilme(Filme filme) {
        return avaliacaoRepository.findAvaliacoesComComentariosByFilme(filme);
    }

    /**
     * Calcular média de notas de um filme
     */
    public Double calcularMediaNotasFilme(Filme filme) {
        Double media = avaliacaoRepository.calcularMediaNotasFilme(filme);
        return media != null ? Math.round(media * 100.0) / 100.0 : 0.0; // Arredondar para 2 casas decimais
    }

    /**
     * Contar total de avaliações de um filme
     */
    public Long contarAvaliacoesFilme(Filme filme) {
        return avaliacaoRepository.countByFilme(filme);
    }

    /**
     * Obter estatísticas de notas de um filme
     */
    public List<Object[]> getEstatisticasNotasByFilme(Filme filme) {
        return avaliacaoRepository.getEstatisticasNotasByFilme(filme);
    }

    /**
     * Atualizar ou criar avaliação (upsert)
     */
    public Avaliacao criarOuAtualizarAvaliacao(Integer nota, String comentario, Usuario usuario, Filme filme) {
        Optional<Avaliacao> avaliacaoExistente = getAvaliacaoByUsuarioAndFilme(usuario, filme);

        if (avaliacaoExistente.isPresent()) {
            // Atualizar avaliação existente
            Avaliacao avaliacao = avaliacaoExistente.get();
            avaliacao.setNota(nota);
            avaliacao.setComentario(comentario);
            return update(avaliacao.getId(), avaliacao);
        } else {
            // Criar nova avaliação
            return create(nota, comentario, usuario, filme);
        }
    }

    /**
     * Deletar avaliação de um usuário para um filme específico
     */
    public void deletarAvaliacaoByUsuarioAndFilme(Usuario usuario, Filme filme) {
        Optional<Avaliacao> avaliacao = getAvaliacaoByUsuarioAndFilme(usuario, filme);
        if (avaliacao.isPresent()) {
            delete(avaliacao.get().getId());
        } else {
            throw new RuntimeException("Avaliação não encontrada para este usuário e filme");
        }
    }
}