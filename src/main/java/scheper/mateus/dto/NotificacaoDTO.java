package scheper.mateus.dto;

import scheper.mateus.entity.Notificacao;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class NotificacaoDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long idNotificacao;

    private String conteudo;

    private String url;

    private LocalDateTime horario;

    public NotificacaoDTO() {
    }

    public NotificacaoDTO(Notificacao notificacao) {
        this.idNotificacao = notificacao.getIdNotificacao();
        this.conteudo = notificacao.getConteudo();
        this.url = notificacao.getUrl();
        this.horario = notificacao.getHorario();
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getHorario() {
        return horario;
    }

    public void setHorario(LocalDateTime horario) {
        this.horario = horario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificacaoDTO that = (NotificacaoDTO) o;
        return Objects.equals(idNotificacao, that.idNotificacao) && Objects.equals(conteudo, that.conteudo) && Objects.equals(url, that.url) && Objects.equals(horario, that.horario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idNotificacao, conteudo, url, horario);
    }
}