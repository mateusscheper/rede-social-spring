package scheper.mateus.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ComentarioDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long idComentario;

    private Long idUsuario;

    private String nomeUsuario;

    private String fotoUsuario;

    private String descricao;

    private List<ReacaoDTO> reacoes = new ArrayList<>();

    public ComentarioDTO() {
    }

    public ComentarioDTO(Long idComentario, Long idUsuario, String nomeUsuario, String fotoUsuario, String descricao) {
        this.idComentario = idComentario;
        this.idUsuario = idUsuario;
        this.nomeUsuario = nomeUsuario;
        this.fotoUsuario = fotoUsuario;
        this.descricao = descricao;
    }

    public Long getIdComentario() {
        return idComentario;
    }

    public void setIdComentario(Long idComentario) {
        this.idComentario = idComentario;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getFotoUsuario() {
        return fotoUsuario;
    }

    public void setFotoUsuario(String fotoUsuario) {
        this.fotoUsuario = fotoUsuario;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<ReacaoDTO> getReacoes() {
        return reacoes;
    }

    public void setReacoes(List<ReacaoDTO> reacoes) {
        this.reacoes = reacoes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComentarioDTO that = (ComentarioDTO) o;
        return Objects.equals(idUsuario, that.idUsuario) && Objects.equals(nomeUsuario, that.nomeUsuario) && Objects.equals(fotoUsuario, that.fotoUsuario) && Objects.equals(descricao, that.descricao) && Objects.equals(reacoes, that.reacoes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, nomeUsuario, fotoUsuario, descricao, reacoes);
    }
}