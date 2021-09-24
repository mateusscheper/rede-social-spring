package scheper.mateus.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class NovoUsuarioDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Long idUsuario;

    @NotEmpty(message = "{usuario.validacao.nomeVazio}")
    private String nome;

    @Email(message = "{usuario.validacao.emailInvalido}")
    @NotEmpty(message = "{usuario.validacao.emailVazio}")
    private String email;

    @NotNull(message = "{usuario.validacao.dataNascimentoVazia}")
    private LocalDate dataNascimento;

    public NovoUsuarioDTO() {
    }

    public NovoUsuarioDTO(Long idUsuario, String nome, String email, LocalDate dataNascimento) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.email = email;
        this.dataNascimento = dataNascimento;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NovoUsuarioDTO that = (NovoUsuarioDTO) o;
        return Objects.equals(idUsuario, that.idUsuario) && Objects.equals(nome, that.nome) && Objects.equals(email, that.email) && Objects.equals(dataNascimento, that.dataNascimento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, nome, email, dataNascimento);
    }
}
