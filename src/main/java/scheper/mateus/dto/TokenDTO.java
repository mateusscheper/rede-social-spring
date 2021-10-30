package scheper.mateus.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class TokenDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long idUsuario;

    private String token;

    public TokenDTO() {
    }

    public TokenDTO(Long idUsuario, String token) {
        this.idUsuario = idUsuario;
        this.token = token;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenDTO tokenDTO = (TokenDTO) o;
        return Objects.equals(idUsuario, tokenDTO.idUsuario) && Objects.equals(token, tokenDTO.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, token);
    }
}