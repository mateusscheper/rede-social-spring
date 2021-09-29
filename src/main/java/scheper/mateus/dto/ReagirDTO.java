package scheper.mateus.dto;

import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class ReagirDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "{reacao.validacao.usuario")
    private Long idUsuario;

    @NotNull(message = "{reacao.validacao.marcado}")
    private boolean marcado;

    @NotNull(message = "{reacao.validacao.post")
    private Long idPost;

    @NotNull(message = "{reacao.validacao.reacao")
    private Long idReacao;

    public ReagirDTO() {
    }

    public ReagirDTO(Long idUsuario, boolean marcado, Long idPost, Long idReacao) {
        this.idUsuario = idUsuario;
        this.marcado = marcado;
        this.idPost = idPost;
        this.idReacao = idReacao;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public boolean isMarcado() {
        return marcado;
    }

    public void setMarcado(boolean marcado) {
        this.marcado = marcado;
    }

    public Long getIdPost() {
        return idPost;
    }

    public void setIdPost(Long idPost) {
        this.idPost = idPost;
    }

    public Long getIdReacao() {
        return idReacao;
    }

    public void setIdReacao(Long idReacao) {
        this.idReacao = idReacao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReagirDTO reagirDTO = (ReagirDTO) o;
        return Objects.equals(idUsuario, reagirDTO.idUsuario) && Objects.equals(idPost, reagirDTO.idPost) && Objects.equals(idReacao, reagirDTO.idReacao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, idPost, idReacao);
    }
}