package scheper.mateus.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class LoginDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private UsuarioSimplesDTO usuario;

    private String token;

    public LoginDTO(UsuarioSimplesDTO usuario, String token) {
        this.usuario = usuario;
        this.token = token;
    }

    public UsuarioSimplesDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSimplesDTO usuario) {
        this.usuario = usuario;
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
        LoginDTO loginDTO = (LoginDTO) o;
        return Objects.equals(usuario, loginDTO.usuario) && Objects.equals(token, loginDTO.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuario, token);
    }
}