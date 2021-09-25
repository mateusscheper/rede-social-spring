package scheper.mateus.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import static scheper.mateus.utils.NumberUtils.castBigIntegerToLong;

public class PostDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long idPost;

    private String descricao;

    private String arquivo;

    private UsuarioDTO criador;

    public PostDTO() {
    }

    public PostDTO(Long idPost, String descricao, String arquivo, UsuarioDTO criador) {
        this.idPost = idPost;
        this.descricao = descricao;
        this.arquivo = arquivo;
        this.criador = criador;
    }

    public PostDTO(Object[] dadosPost) {
        this.idPost = castBigIntegerToLong(dadosPost[0]);
        this.descricao = (String) dadosPost[1];
        this.arquivo = (String) dadosPost[2];
        this.criador = new UsuarioDTO(dadosPost);
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

    public String getArquivo() {
        return arquivo;
    }

    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }

    public UsuarioDTO getCriador() {
        return criador;
    }

    public void setCriador(UsuarioDTO criador) {
        this.criador = criador;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostDTO postDTO = (PostDTO) o;
        return Objects.equals(idPost, postDTO.idPost) && Objects.equals(descricao, postDTO.descricao) && Objects.equals(criador, postDTO.criador);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPost, descricao, criador);
    }
}
