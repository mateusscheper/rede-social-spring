package scheper.mateus.entity;

import scheper.mateus.dto.ReportDTO;
import scheper.mateus.enums.ReportStatusEnum;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(schema = "api", name = "report")
public class Report {

    @Id
    @GeneratedValue
    private Long idReport;

    @NotEmpty
    private String conteudo;

    @NotNull
    private LocalDateTime horario;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    private Comentario comentario;

    @NotEmpty
    private String status;

    public Report() {
    }

    public Report(ReportDTO reportDTO, Usuario usuario, Post post, Comentario comentario) {
        this.conteudo = reportDTO.getConteudo();
        this.horario = LocalDateTime.now();
        this.usuario = usuario;
        this.post = post;
        this.comentario = comentario;
        this.status = ReportStatusEnum.ABERTO.getDescricao();
    }

    public Long getIdReport() {
        return idReport;
    }

    public void setIdReport(Long idReport) {
        this.idReport = idReport;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public LocalDateTime getHorario() {
        return horario;
    }

    public void setHorario(LocalDateTime horario) {
        this.horario = horario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Comentario getComentario() {
        return comentario;
    }

    public void setComentario(Comentario comentario) {
        this.comentario = comentario;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return Objects.equals(idReport, report.idReport) && Objects.equals(conteudo, report.conteudo) && Objects.equals(horario, report.horario) && Objects.equals(usuario, report.usuario) && Objects.equals(post, report.post) && Objects.equals(comentario, report.comentario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idReport, conteudo, horario, usuario, post, comentario);
    }
}