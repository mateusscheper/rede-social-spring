package scheper.mateus.entity;

import scheper.mateus.enums.ReacaoEnum;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(schema = "api", name = "reacao")
public class Reacao {

    @Id
    @GeneratedValue
    private Long idReacao;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Post post;

    @Column(name = "tipo", nullable = false)
    private String tipo;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "reacao_usuarios",
            schema = "api",
            joinColumns = {
                    @JoinColumn(name = "id_reacao")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "id_usuario")
            })
    private List<Usuario> usuarios = new ArrayList<>();

    @Column(name = "criacao", nullable = false)
    private LocalDateTime criacao;

    public Reacao() {
    }

    public Reacao(ReacaoEnum reacaoEnum) {
        this.tipo = reacaoEnum.getNome();
        this.criacao = LocalDateTime.now();
    }

    public Long getIdReacao() {
        return idReacao;
    }

    public void setIdReacao(Long idReacao) {
        this.idReacao = idReacao;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuario) {
        this.usuarios = usuario;
    }

    public LocalDateTime getCriacao() {
        return criacao;
    }

    public void setCriacao(LocalDateTime criacao) {
        this.criacao = criacao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reacao reacao = (Reacao) o;
        return Objects.equals(idReacao, reacao.idReacao) && Objects.equals(post, reacao.post);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idReacao, post);
    }
}