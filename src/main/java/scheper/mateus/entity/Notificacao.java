package scheper.mateus.entity;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity()
@Table(schema = "api", name = "notificacao")
public class Notificacao {

    @Id
    @GeneratedValue
    @Column(name = "id_notificacao")
    private Long idNotificacao;

    @NotEmpty
    @Column(name = "conteudo")
    private String conteudo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_criador")
    @NotNull
    private Usuario criador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_recebedor")
    private Usuario recebedor;

    @NotNull
    private LocalDateTime horario;

    @NotEmpty
    private String url;

    public Notificacao() {
    }

    public Notificacao(Usuario usuarioLogado, Usuario usuarioAdicionado, String conteudo, String url) {
        this.criador = usuarioLogado;
        this.recebedor = usuarioAdicionado;
        this.conteudo = conteudo;
        this.horario = LocalDateTime.now();
        this.url = url;
    }

    public Long getIdNotificacao() {
        return idNotificacao;
    }

    public void setIdNotificacao(Long idNotificacao) {
        this.idNotificacao = idNotificacao;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public Usuario getCriador() {
        return criador;
    }

    public void setCriador(Usuario criador) {
        this.criador = criador;
    }

    public Usuario getRecebedor() {
        return recebedor;
    }

    public void setRecebedor(Usuario recebedor) {
        this.recebedor = recebedor;
    }

    public LocalDateTime getHorario() {
        return horario;
    }

    public void setHorario(LocalDateTime horario) {
        this.horario = horario;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notificacao that = (Notificacao) o;
        return Objects.equals(idNotificacao, that.idNotificacao) && Objects.equals(conteudo, that.conteudo) && Objects.equals(criador, that.criador) && Objects.equals(recebedor, that.recebedor) && Objects.equals(horario, that.horario) && Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idNotificacao, conteudo, criador, recebedor, horario, url);
    }
}