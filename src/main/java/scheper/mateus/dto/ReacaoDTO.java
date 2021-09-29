package scheper.mateus.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class ReacaoDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long idReacao;

    private String nome;

    private String icone;

    private Integer quantidade;

    private boolean marcado;

    public Long getIdReacao() {
        return idReacao;
    }

    public void setIdReacao(Long idReacao) {
        this.idReacao = idReacao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIcone() {
        return icone;
    }

    public void setIcone(String icone) {
        this.icone = icone;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public boolean isMarcado() {
        return marcado;
    }

    public void setMarcado(boolean marcado) {
        this.marcado = marcado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReacaoDTO reacaoDTO = (ReacaoDTO) o;
        return Objects.equals(idReacao, reacaoDTO.idReacao) && Objects.equals(nome, reacaoDTO.nome) && Objects.equals(icone, reacaoDTO.icone) && Objects.equals(quantidade, reacaoDTO.quantidade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idReacao, nome, icone, quantidade);
    }
}