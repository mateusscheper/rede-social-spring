package scheper.mateus.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PostCompletoDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long idPost;

    private String descricao;

    private String arquivo;

    private UsuarioDTO criador;

    private LocalDateTime criacao;

    private List<ComentarioDTO> comentarios = new ArrayList<>();

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

    public LocalDateTime getCriacao() {
        return criacao;
    }

    public void setCriacao(LocalDateTime criacao) {
        this.criacao = criacao;
    }

    public List<ComentarioDTO> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<ComentarioDTO> comentarios) {
        this.comentarios = comentarios;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostCompletoDTO that = (PostCompletoDTO) o;
        return Objects.equals(idPost, that.idPost) && Objects.equals(descricao, that.descricao) && Objects.equals(arquivo, that.arquivo) && Objects.equals(criador, that.criador) && Objects.equals(criacao, that.criacao) && Objects.equals(comentarios, that.comentarios);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPost, descricao, arquivo, criador, criacao, comentarios);
    }
}
