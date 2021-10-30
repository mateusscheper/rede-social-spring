package scheper.mateus.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class LoginRegistroDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Email(message = "{usuario.validacao.emailInvalido}")
    @NotBlank(message = "{usuario.validacao.emailVazio}")
    private String email;

    @NotBlank(message = "{usuario.validacao.senhaVazia}")
    private String senha;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginRegistroDTO loginDTO = (LoginRegistroDTO) o;
        return Objects.equals(email, loginDTO.email) && Objects.equals(senha, loginDTO.senha);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, senha);
    }
}