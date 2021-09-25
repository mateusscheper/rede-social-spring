package scheper.mateus.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class NovoComentarioDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "{comentario.validacao.usuario}")
    private Long idUsuario;

    @NotNull(message = "{comentario.validacao.post}")
    private Long idPost;

    @NotEmpty(message = "{comentario.validacao.descricao}")
    private String descricao;

    public NovoComentarioDTO() {
    }

    public NovoComentarioDTO(Long idUsuario, Long idPost, String descricao) {
        this.idUsuario = idUsuario;
        this.idPost = idPost;
        this.descricao = descricao;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Long getIdPost() {
        return idPost;
    }

    public void setIdPost(Long idPost) {
        this.idPost = idPost;
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
        NovoComentarioDTO that = (NovoComentarioDTO) o;
        return Objects.equals(idUsuario, that.idUsuario) && Objects.equals(idPost, that.idPost) && Objects.equals(descricao, that.descricao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, idPost, descricao);
    }
}