package scheper.mateus.dto;

import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class NovoPostDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "{post.validacao.usuario}")
    private Long idUsuario;

    private String descricao;

    public NovoPostDTO() {
    }

    public NovoPostDTO(Long idUsuario, String descricao) {
        this.idUsuario = idUsuario;
        this.descricao = descricao;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
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
        NovoPostDTO that = (NovoPostDTO) o;
        return Objects.equals(idUsuario, that.idUsuario) && Objects.equals(descricao, that.descricao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, descricao);
    }
}