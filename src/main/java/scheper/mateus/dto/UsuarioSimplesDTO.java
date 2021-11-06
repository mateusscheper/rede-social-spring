package scheper.mateus.dto;

import scheper.mateus.entity.Usuario;

import java.io.Serial;
import java.io.Serializable;

public class UsuarioSimplesDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long idUsuario;

    private String nome;

    private String email;

    private String foto;

    public UsuarioSimplesDTO(Usuario usuario, boolean isFotoNormal) {
        this.idUsuario = usuario.getIdUsuario();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();

        if (usuario.getFoto() != null) {
            if (isFotoNormal)
                this.foto = usuario.getFoto().getCaminho();
            else
                this.foto = usuario.getFoto().getCaminhoCrop();
        } else
            this.foto = "assets/nopic.png";
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

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}