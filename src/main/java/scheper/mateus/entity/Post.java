package scheper.mateus.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(schema = "api", name = "post")
public class Post {

    @Id
    @GeneratedValue
    private Long idPost;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Usuario criador;

    @Column(name = "criacao", nullable = false)
    private LocalDateTime criacao;

    @Column(name = "exclusao")
    private LocalDateTime exclusao;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Usuario> visualizadores = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "post_reacoes",
            schema = "api",
            joinColumns = {
                    @JoinColumn(name = "id_post")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "id_reacao")
            })
    private List<Reacao> reacoes = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "post_arquivos",
            schema = "api",
            joinColumns = {
                    @JoinColumn(name = "id_post")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "id_arquivo")
            })
    private List<Arquivo> arquivos = new ArrayList<>();

    @Column(name = "descricao")
    private String descricao;

    public Long getIdPost() {
        return idPost;
    }

    public void setIdPost(Long idPost) {
        this.idPost = idPost;
    }

    public Usuario getCriador() {
        return criador;
    }

    public void setCriador(Usuario criador) {
        this.criador = criador;
    }

    public LocalDateTime getCriacao() {
        return criacao;
    }

    public void setCriacao(LocalDateTime criacao) {
        this.criacao = criacao;
    }

    public LocalDateTime getExclusao() {
        return exclusao;
    }

    public void setExclusao(LocalDateTime exclusao) {
        this.exclusao = exclusao;
    }

    public List<Usuario> getVisualizadores() {
        return visualizadores;
    }

    public void setVisualizadores(List<Usuario> visualizadores) {
        this.visualizadores = visualizadores;
    }

    public List<Reacao> getReacoes() {
        return reacoes;
    }

    public void setReacoes(List<Reacao> reacoes) {
        this.reacoes = reacoes;
    }

    public List<Arquivo> getArquivos() {
        return arquivos;
    }

    public void setArquivos(List<Arquivo> arquivos) {
        this.arquivos = arquivos;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(idPost, post.idPost);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPost);
    }
}