package scheper.mateus.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class ReportDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "{report.validacao.conteudo}")
    private String conteudo;

    @NotEmpty(message = "{report.validacao.tipo}")
    private String tipo;

    @NotNull(message = "{report.validacao.id}")
    private Long id;

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportDTO reportDTO = (ReportDTO) o;
        return Objects.equals(conteudo, reportDTO.conteudo) && Objects.equals(tipo, reportDTO.tipo) && Objects.equals(id, reportDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conteudo, tipo, id);
    }
}