package com.streamflix.api.service;

import com.streamflix.api.entity.ListaFavoritos;
import com.streamflix.api.entity.Usuario;
import com.streamflix.api.repository.ListaFavoritosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ListaFavoritosService {

    @Autowired
    private ListaFavoritosRepository listaFavoritosRepository;

    // MÉTODOS CRUD OBRIGATÓRIOS

    /**
     * GET ONE - Buscar lista de favoritos por ID
     */
    public Optional<ListaFavoritos> getOne(Long id) {
        return listaFavoritosRepository.findById(id);
    }

    /**
     * GET ALL - Buscar todas as listas de favoritos
     */
    public List<ListaFavoritos> getAll() {
        return listaFavoritosRepository.findAll();
    }

    /**
     * CREATE - Criar nova lista de favoritos
     */
    public ListaFavoritos create(ListaFavoritos listaFavoritos) {
        // Definir como pública por padrão se não especificado
        if (listaFavoritos.getPublica() == null) {
            listaFavoritos.setPublica(true);
        }

        return listaFavoritosRepository.save(listaFavoritos);
    }

    /**
     * CREATE - Criar lista com parâmetros separados
     */
    public ListaFavoritos create(String nome, String descricao, Boolean publica, Usuario usuario) {
        ListaFavoritos lista = new ListaFavoritos(nome, descricao, publica, usuario);
        return create(lista);
    }

    /**
     * UPDATE - Atualizar lista de favoritos existente
     */
    public ListaFavoritos update(Long id, ListaFavoritos listaAtualizada) {
        ListaFavoritos listaExistente = listaFavoritosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lista de favoritos não encontrada com ID: " + id));

        // Atualizar campos (não permite alterar usuário proprietário)
        listaExistente.setNome(listaAtualizada.getNome());
        listaExistente.setDescricao(listaAtualizada.getDescricao());

        if (listaAtualizada.getPublica() != null) {
            listaExistente.setPublica(listaAtualizada.getPublica());
        }

        return listaFavoritosRepository.save(listaExistente);
    }

    /**
     * DELETE - Deletar lista de favoritos
     */
    public void delete(Long id) {
        if (!listaFavoritosRepository.existsById(id)) {
            throw new RuntimeException("Lista de favoritos não encontrada com ID: " + id);
        }
        listaFavoritosRepository.deleteById(id);
    }

    // MÉTODOS AUXILIARES

    /**
     * Buscar listas de um usuário específico
     */
    public List<ListaFavoritos> getListasByUsuario(Usuario usuario) {
        return listaFavoritosRepository.findByUsuario(usuario);
    }

    /**
     * Buscar listas de um usuário ordenadas por data de criação (mais recentes primeiro)
     */
    public List<ListaFavoritos> getListasByUsuarioOrdenadas(Usuario usuario) {
        return listaFavoritosRepository.findByUsuarioOrderByDataCriacaoDesc(usuario);
    }

    /**
     * Buscar apenas listas públicas
     */
    public List<ListaFavoritos> getListasPublicas() {
        return listaFavoritosRepository.findByPublicaTrue();
    }

    /**
     * Buscar listas públicas ordenadas por data de criação
     */
    public List<ListaFavoritos> getListasPublicasOrdenadas() {
        return listaFavoritosRepository.findByPublicaTrueOrderByDataCriacaoDesc();
    }

    /**
     * Buscar listas públicas de outros usuários (exceto o próprio)
     */
    public List<ListaFavoritos> getListasPublicasDeOutrosUsuarios(Usuario usuario) {
        return listaFavoritosRepository.findListasPublicasDeOutrosUsuarios(usuario);
    }

    /**
     * Buscar listas por nome (case-insensitive)
     */
    public List<ListaFavoritos> getListasByNome(String nome) {
        return listaFavoritosRepository.findByNomeContainingIgnoreCase(nome);
    }

    /**
     * Buscar listas públicas por nome
     */
    public List<ListaFavoritos> getListasPublicasByNome(String nome) {
        return listaFavoritosRepository.findByNomeContainingIgnoreCaseAndPublicaTrue(nome);
    }

    /**
     * Contar quantas listas um usuário possui
     */
    public Long contarListasUsuario(Usuario usuario) {
        return listaFavoritosRepository.countByUsuario(usuario);
    }

    /**
     * Verificar se usuário é proprietário da lista
     */
    public boolean isProprietario(Long listaId, Usuario usuario) {
        Optional<ListaFavoritos> lista = getOne(listaId);
        return lista.isPresent() && lista.get().getUsuario().getId().equals(usuario.getId());
    }

    /**
     * Verificar se lista é acessível pelo usuário (própria ou pública)
     */
    public boolean isAcessivel(Long listaId, Usuario usuario) {
        Optional<ListaFavoritos> lista = getOne(listaId);
        if (lista.isEmpty()) {
            return false;
        }

        ListaFavoritos listaFavoritos = lista.get();
        // Acessível se for do próprio usuário OU se for pública
        return listaFavoritos.getUsuario().getId().equals(usuario.getId()) ||
                listaFavoritos.getPublica();
    }

    /**
     * Tornar lista pública
     */
    public ListaFavoritos tornarPublica(Long id) {
        ListaFavoritos lista = listaFavoritosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lista de favoritos não encontrada com ID: " + id));

        lista.setPublica(true);
        return listaFavoritosRepository.save(lista);
    }

    /**
     * Tornar lista privada
     */
    public ListaFavoritos tornarPrivada(Long id) {
        ListaFavoritos lista = listaFavoritosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lista de favoritos não encontrada com ID: " + id));

        lista.setPublica(false);
        return listaFavoritosRepository.save(lista);
    }

    /**
     * Validar permissão de acesso (para controllers)
     */
    public ListaFavoritos validarAcesso(Long listaId, Usuario usuario) {
        ListaFavoritos lista = listaFavoritosRepository.findById(listaId)
                .orElseThrow(() -> new RuntimeException("Lista de favoritos não encontrada com ID: " + listaId));

        // Verificar se usuário tem acesso (própria lista ou pública)
        if (!lista.getUsuario().getId().equals(usuario.getId()) && !lista.getPublica()) {
            throw new RuntimeException("Acesso negado: Lista privada de outro usuário");
        }

        return lista;
    }

    /**
     * Validar permissão de edição (apenas proprietário)
     */
    public ListaFavoritos validarPermissaoEdicao(Long listaId, Usuario usuario) {
        ListaFavoritos lista = listaFavoritosRepository.findById(listaId)
                .orElseThrow(() -> new RuntimeException("Lista de favoritos não encontrada com ID: " + listaId));

        // Verificar se usuário é proprietário
        if (!lista.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acesso negado: Apenas o proprietário pode editar a lista");
        }

        return lista;
    }
}