package scheper.mateus.dto;

import scheper.mateus.utils.NumberUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class ReacaoDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long idUsuario;

    private String nomeUsuario;

    private String tipo;

    public ReacaoDTO() {
    }

    public ReacaoDTO(Long idUsuario, String nomeUsuario, String tipo) {
        this.idUsuario = idUsuario;
        this.nomeUsuario = nomeUsuario;
        this.tipo = tipo;
    }

    public ReacaoDTO(Object[] dados) {
        this.idUsuario = NumberUtils.castBigIntegerToLong(dados[3]);
        this.nomeUsuario = (String) dados[4];
        this.tipo = (String) dados[1];
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReacaoDTO reacaoDTO = (ReacaoDTO) o;
        return Objects.equals(idUsuario, reacaoDTO.idUsuario) && Objects.equals(nomeUsuario, reacaoDTO.nomeUsuario) && Objects.equals(tipo, reacaoDTO.tipo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, nomeUsuario, tipo);
    }
}