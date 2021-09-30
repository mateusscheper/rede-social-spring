package scheper.mateus.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(schema = "api", name = "arquivo")
public class Arquivo {

    @Id
    @GeneratedValue
    private Long idArquivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_dono")
    private Usuario dono;

    @Column(name = "nome_real", nullable = false)
    private String nomeReal;

    @Column(name = "nome_codificado", nullable = false)
    private String nomeCodificado;

    @Column(name = "caminho", nullable = false)
    private String caminho;

    @Column(name = "tipo", nullable = false)
    private String tipo;

    @Column(name = "tamanho", nullable = false)
    private Long tamanho;

    public Long getIdArquivo() {
        return idArquivo;
    }

    public void setIdArquivo(Long idArquivo) {
        this.idArquivo = idArquivo;
    }

    public Usuario getDono() {
        return dono;
    }

    public void setDono(Usuario dono) {
        this.dono = dono;
    }

    public String getNomeReal() {
        return nomeReal;
    }

    public void setNomeReal(String nome) {
        this.nomeReal = nome;
    }

    public String getNomeCodificado() {
        return nomeCodificado;
    }

    public void setNomeCodificado(String nomeCodificado) {
        this.nomeCodificado = nomeCodificado;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Long getTamanho() {
        return tamanho;
    }

    public void setTamanho(Long tamanho) {
        this.tamanho = tamanho;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arquivo arquivo = (Arquivo) o;
        return Objects.equals(idArquivo, arquivo.idArquivo) && Objects.equals(nomeReal, arquivo.nomeReal) && Objects.equals(caminho, arquivo.caminho) && Objects.equals(tipo, arquivo.tipo) && Objects.equals(tamanho, arquivo.tamanho);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idArquivo, nomeReal, caminho, tipo, tamanho);
    }
}