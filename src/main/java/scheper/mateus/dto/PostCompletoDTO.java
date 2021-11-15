package scheper.mateus.dto;

import scheper.mateus.entity.Post;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static scheper.mateus.utils.NumberUtils.castBigIntegerToLong;

public class PostCompletoDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long idPost;

    private String descricao;

    private String arquivo;

    private UsuarioDTO criador;

    private LocalDateTime criacao;

    private List<ReacaoDTO> reacoes = new ArrayList<>();

    private List<ComentarioDTO> comentarios = new ArrayList<>();

    public PostCompletoDTO() {
    }

    public PostCompletoDTO(Object[] dadosPost) {
        this.idPost = castBigIntegerToLong(dadosPost[0]);
        this.descricao = (String) dadosPost[1];
        this.arquivo = (String) dadosPost[2];
        this.criador = new UsuarioDTO(dadosPost);
        this.criacao = ((Timestamp) dadosPost[6]).toLocalDateTime();
    }

    public PostCompletoDTO(Post post) {
        this.idPost = post.getIdPost();
        this.descricao = post.getDescricao();
        this.arquivo = !post.getArquivos().isEmpty() ? post.getArquivos().get(0).getCaminho() : null;
        this.criador = new UsuarioDTO(post.getUsuario());
        this.criacao = post.getCriacao();
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

    public LocalDateTime getCriacao() {
        return criacao;
    }

    public void setCriacao(LocalDateTime criacao) {
        this.criacao = criacao;
    }

    public List<ReacaoDTO> getReacoes() {
        return reacoes;
    }

    public void setReacoes(List<ReacaoDTO> reacoes) {
        this.reacoes = reacoes;
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
