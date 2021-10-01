package scheper.mateus.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import static scheper.mateus.utils.NumberUtils.castBigIntegerToLong;

public class UsuarioDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long idUsuario;

    private String nome;

    private String foto;

    public UsuarioDTO(Long idUsuario, String nome, String foto) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.foto = foto;
    }

    public UsuarioDTO(Object[] dadosPost) {
        if (dadosPost.length == 3) {
            this.idUsuario = castBigIntegerToLong(dadosPost[0]);
            this.nome = (String) dadosPost[1];
            this.foto = (String) dadosPost[2];
        } else {
            this.idUsuario = castBigIntegerToLong(dadosPost[3]);
            this.nome = (String) dadosPost[4];
            this.foto = (String) dadosPost[5];
        }
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

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioDTO that = (UsuarioDTO) o;
        return Objects.equals(idUsuario, that.idUsuario) && Objects.equals(nome, that.nome) && Objects.equals(foto, that.foto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, nome, foto);
    }
}
