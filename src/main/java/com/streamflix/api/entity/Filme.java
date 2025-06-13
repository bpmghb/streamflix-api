package com.streamflix.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "filmes")
public class Filme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Título é obrigatório")
    @Size(max = 255, message = "Título deve ter no máximo 255 caracteres")
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(length = 100)
    private String genero;

    @Column(name = "ano_lancamento")
    @Min(value = 1900, message = "Ano de lançamento deve ser maior que 1900")
    private Integer anoLancamento;

    @Column
    @Min(value = 1, message = "Duração deve ser maior que 0")
    private Integer duracao; // em minutos

    @Column(length = 150)
    private String diretor;

    @Column(name = "url_poster", length = 500)
    private String urlPoster;

    @Column(nullable = false)
    private Boolean ativo = true;

    // CAMPO OBRIGATÓRIO PARA O RANKING DE POPULARIDADE
    @Column(name = "contador_acessos", nullable = false)
    private Long contadorAcessos = 0L;

    @CreationTimestamp
    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    // Relacionamentos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criado_por")
    private Usuario criadoPor;

    @OneToMany(mappedBy = "filme", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Avaliacao> avaliacoes;

    @OneToMany(mappedBy = "filme", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ListaFavoritosFilmes> listasFavoritos;

    // Construtores
    public Filme() {}

    public Filme(String titulo, String descricao, String genero, Integer anoLancamento,
                 Integer duracao, String diretor, String urlPoster, Usuario criadoPor) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.genero = genero;
        this.anoLancamento = anoLancamento;
        this.duracao = duracao;
        this.diretor = diretor;
        this.urlPoster = urlPoster;
        this.criadoPor = criadoPor;
    }

    // Método para incrementar contador de acessos (REQUISITO OBRIGATÓRIO)
    public void incrementarAcessos() {
        this.contadorAcessos++;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public Integer getAnoLancamento() {
        return anoLancamento;
    }

    public void setAnoLancamento(Integer anoLancamento) {
        this.anoLancamento = anoLancamento;
    }

    public Integer getDuracao() {
        return duracao;
    }

    public void setDuracao(Integer duracao) {
        this.duracao = duracao;
    }

    public String getDiretor() {
        return diretor;
    }

    public void setDiretor(String diretor) {
        this.diretor = diretor;
    }

    public String getUrlPoster() {
        return urlPoster;
    }

    public void setUrlPoster(String urlPoster) {
        this.urlPoster = urlPoster;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Long getContadorAcessos() {
        return contadorAcessos;
    }

    public void setContadorAcessos(Long contadorAcessos) {
        this.contadorAcessos = contadorAcessos;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public Usuario getCriadoPor() {
        return criadoPor;
    }

    public void setCriadoPor(Usuario criadoPor) {
        this.criadoPor = criadoPor;
    }

    public List<Avaliacao> getAvaliacoes() {
        return avaliacoes;
    }

    public void setAvaliacoes(List<Avaliacao> avaliacoes) {
        this.avaliacoes = avaliacoes;
    }

    public List<ListaFavoritosFilmes> getListasFavoritos() {
        return listasFavoritos;
    }

    public void setListasFavoritos(List<ListaFavoritosFilmes> listasFavoritos) {
        this.listasFavoritos = listasFavoritos;
    }
}