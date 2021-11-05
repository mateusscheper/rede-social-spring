package scheper.mateus.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class FotoPerfilDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private String foto;

    private String fotoCrop;

    public FotoPerfilDTO(String foto, String fotoCrop) {
        this.foto = foto;
        this.fotoCrop = fotoCrop;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getFotoCrop() {
        return fotoCrop;
    }

    public void setFotoCrop(String fotoCrop) {
        this.fotoCrop = fotoCrop;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FotoPerfilDTO that = (FotoPerfilDTO) o;
        return Objects.equals(foto, that.foto) && Objects.equals(fotoCrop, that.fotoCrop);
    }

    @Override
    public int hashCode() {
        return Objects.hash(foto, fotoCrop);
    }
}