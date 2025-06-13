package com.streamflix.api.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "lista_favoritos_filmes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"lista_favoritos_id", "filme_id"}))
public class ListaFavoritosFilmes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "data_adicao", updatable = false)
    private LocalDateTime dataAdicao;

    // Relacionamentos
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lista_favoritos_id", nullable = false)
    private ListaFavoritos listaFavoritos;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "filme_id", nullable = false)
    private Filme filme;

    // Construtores
    public ListaFavoritosFilmes() {}

    public ListaFavoritosFilmes(ListaFavoritos listaFavoritos, Filme filme) {
        this.listaFavoritos = listaFavoritos;
        this.filme = filme;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataAdicao() {
        return dataAdicao;
    }

    public void setDataAdicao(LocalDateTime dataAdicao) {
        this.dataAdicao = dataAdicao;
    }

    public ListaFavoritos getListaFavoritos() {
        return listaFavoritos;
    }

    public void setListaFavoritos(ListaFavoritos listaFavoritos) {
        this.listaFavoritos = listaFavoritos;
    }

    public Filme getFilme() {
        return filme;
    }

    public void setFilme(Filme filme) {
        this.filme = filme;
    }
}