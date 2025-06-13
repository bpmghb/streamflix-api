package com.streamflix.api.service;

import com.streamflix.api.entity.ListaFavoritosFilmes;
import com.streamflix.api.entity.ListaFavoritos;
import com.streamflix.api.entity.Filme;
import com.streamflix.api.repository.ListaFavoritosFilmesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ListaFavoritosFilmesService {

    @Autowired
    private ListaFavoritosFilmesRepository listaFavoritosFilmesRepository;

    // MÉTODOS CRUD OBRIGATÓRIOS

    /**
     * GET ONE - Buscar relação por ID
     */
    public Optional<ListaFavoritosFilmes> getOne(Long id) {
        return listaFavoritosFilmesRepository.findById(id);
    }

    /**
     * GET ALL - Buscar todas as relações
     */
    public List<ListaFavoritosFilmes> getAll() {
        return listaFavoritosFilmesRepository.findAll();
    }

    /**
     * CREATE - Adicionar filme à lista de favoritos
     */
    public ListaFavoritosFilmes create(ListaFavoritosFilmes listaFavoritosFilmes) {
        // Verificar se filme já está na lista
        if (listaFavoritosFilmesRepository.existsByListaFavoritosAndFilme(
                listaFavoritosFilmes.getListaFavoritos(),
                listaFavoritosFilmes.getFilme())) {
            throw new RuntimeException("Filme já está presente nesta lista de favoritos");
        }

        return listaFavoritosFilmesRepository.save(listaFavoritosFilmes);
    }

    /**
     * CREATE - Adicionar filme à lista (método simplificado)
     */
    public ListaFavoritosFilmes adicionarFilmeNaLista(ListaFavoritos listaFavoritos, Filme filme) {
        ListaFavoritosFilmes relacao = new ListaFavoritosFilmes(listaFavoritos, filme);
        return create(relacao);
    }

    /**
     * UPDATE - Atualizar relação (não faz muito sentido para esta entidade)
     */
    public ListaFavoritosFilmes update(Long id, ListaFavoritosFilmes listaAtualizada) {
        ListaFavoritosFilmes relacaoExistente = listaFavoritosFilmesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Relação não encontrada com ID: " + id));

        // Para esta entidade, geralmente só se deleta e cria nova relação
        // Mas implementando update para completar CRUD
        relacaoExistente.setListaFavoritos(listaAtualizada.getListaFavoritos());
        relacaoExistente.setFilme(listaAtualizada.getFilme());

        return listaFavoritosFilmesRepository.save(relacaoExistente);
    }

    /**
     * DELETE - Remover filme da lista de favoritos
     */
    public void delete(Long id) {
        if (!listaFavoritosFilmesRepository.existsById(id)) {
            throw new RuntimeException("Relação não encontrada com ID: " + id);
        }
        listaFavoritosFilmesRepository.deleteById(id);
    }

    // MÉTODOS AUXILIARES

    /**
     * Buscar filmes de uma lista específica
     */
    public List<ListaFavoritosFilmes> getFilmesDaLista(ListaFavoritos listaFavoritos) {
        return listaFavoritosFilmesRepository.findByListaFavoritos(listaFavoritos);
    }

    /**
     * Buscar filmes de uma lista ordenados por data de adição (mais recentes primeiro)
     */
    public List<ListaFavoritosFilmes> getFilmesDaListaOrdenados(ListaFavoritos listaFavoritos) {
        return listaFavoritosFilmesRepository.findByListaFavoritosOrderByDataAdicaoDesc(listaFavoritos);
    }

    /**
     * Buscar listas que contêm um filme específico
     */
    public List<ListaFavoritosFilmes> getListasComFilme(Filme filme) {
        return listaFavoritosFilmesRepository.findByFilme(filme);
    }

    /**
     * Verificar se filme já está em uma lista específica
     */
    public boolean filmeEstaNaLista(ListaFavoritos listaFavoritos, Filme filme) {
        return listaFavoritosFilmesRepository.existsByListaFavoritosAndFilme(listaFavoritos, filme);
    }

    /**
     * Buscar relação específica entre lista e filme
     */
    public Optional<ListaFavoritosFilmes> getRelacaoListaFilme(ListaFavoritos listaFavoritos, Filme filme) {
        return listaFavoritosFilmesRepository.findByListaFavoritosAndFilme(listaFavoritos, filme);
    }

    /**
     * Contar quantos filmes tem em uma lista
     */
    public Long contarFilmesNaLista(ListaFavoritos listaFavoritos) {
        return listaFavoritosFilmesRepository.countByListaFavoritos(listaFavoritos);
    }

    /**
     * Remover filme da lista (método simplificado)
     */
    public void removerFilmeDaLista(ListaFavoritos listaFavoritos, Filme filme) {
        Optional<ListaFavoritosFilmes> relacao = getRelacaoListaFilme(listaFavoritos, filme);
        if (relacao.isPresent()) {
            delete(relacao.get().getId());
        } else {
            throw new RuntimeException("Filme não encontrado nesta lista de favoritos");
        }
    }

    /**
     * Remover filme da lista por IDs
     */
    public void removerFilmeDaLista(Long listaId, Long filmeId, ListaFavoritos listaFavoritos, Filme filme) {
        removerFilmeDaLista(listaFavoritos, filme);
    }

    /**
     * Toggle: Adicionar ou remover filme da lista
     */
    public boolean toggleFilmeNaLista(ListaFavoritos listaFavoritos, Filme filme) {
        if (filmeEstaNaLista(listaFavoritos, filme)) {
            // Se já está na lista, remove
            removerFilmeDaLista(listaFavoritos, filme);
            return false; // Removido
        } else {
            // Se não está na lista, adiciona
            adicionarFilmeNaLista(listaFavoritos, filme);
            return true; // Adicionado
        }
    }

    /**
     * Obter ranking de filmes mais adicionados às listas
     */
    public List<Object[]> getFilmesMaisAdicionadosListas() {
        return listaFavoritosFilmesRepository.findFilmesMaisAdicionadosListas();
    }

    /**
     * Limpar todos os filmes de uma lista
     */
    public void limparLista(ListaFavoritos listaFavoritos) {
        List<ListaFavoritosFilmes> filmesDaLista = getFilmesDaLista(listaFavoritos);
        for (ListaFavoritosFilmes relacao : filmesDaLista) {
            delete(relacao.getId());
        }
    }

    /**
     * Copiar filmes de uma lista para outra
     */
    public void copiarFilmesEntrelistas(ListaFavoritos listaOrigem, ListaFavoritos listaDestino) {
        List<ListaFavoritosFilmes> filmesOrigem = getFilmesDaLista(listaOrigem);

        for (ListaFavoritosFilmes relacao : filmesOrigem) {
            // Adicionar apenas se não estiver já na lista destino
            if (!filmeEstaNaLista(listaDestino, relacao.getFilme())) {
                adicionarFilmeNaLista(listaDestino, relacao.getFilme());
            }
        }
    }
}