package scheper.mateus.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(schema = "api", name = "reacao")
public class Reacao {

    @Id
    @GeneratedValue
    private Long idReacao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private TipoReacao tipo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Usuario usuario;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

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

    public TipoReacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoReacao tipo) {
        this.tipo = tipo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}
