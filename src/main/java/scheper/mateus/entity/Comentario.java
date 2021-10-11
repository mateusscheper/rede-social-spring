package scheper.mateus.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(schema = "api", name = "comentario")
public class Comentario {

    @Id
    @GeneratedValue
    private Long idComentario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario_criador")
    private Usuario criador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_post")
    private Post post;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comentario_pai")
    private Comentario comentarioPai;

    @Column(name = "criacao", nullable = false)
    private LocalDateTime criacao;

    private LocalDateTime exclusao;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "comentario_reacoes",
            schema = "api",
            joinColumns = {
                    @JoinColumn(name = "id_comentario")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "id_reacao")
            })
    private List<Reacao> reacoes;

    @Column(name = "descricao", nullable = false)
    private String descricao;

    public Comentario() {
    }

    public Comentario(Long idComentario, Usuario criador, Post post, LocalDateTime criacao,
                      LocalDateTime exclusao, List<Reacao> reacoes, String descricao) {
        this.idComentario = idComentario;
        this.criador = criador;
        this.post = post;
        this.criacao = criacao;
        this.exclusao = exclusao;
        this.reacoes = reacoes;
        this.descricao = descricao;
    }

    public Long getIdComentario() {
        return idComentario;
    }

    public void setIdComentario(Long idComentario) {
        this.idComentario = idComentario;
    }

    public Usuario getCriador() {
        return criador;
    }

    public void setCriador(Usuario criador) {
        this.criador = criador;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Comentario getComentarioPai() {
        return comentarioPai;
    }

    public void setComentarioPai(Comentario comentario) {
        this.comentarioPai = comentario;
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

    public List<Reacao> getReacoes() {
        return reacoes;
    }

    public void setReacoes(List<Reacao> reacoes) {
        this.reacoes = reacoes;
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
        Comentario that = (Comentario) o;
        return Objects.equals(idComentario, that.idComentario) && Objects.equals(descricao, that.descricao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idComentario, descricao);
    }
}