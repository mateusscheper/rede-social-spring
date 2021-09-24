package scheper.mateus.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(schema = "api", name = "tipo_reacao")
public class TipoReacao {

    @Id
    @GeneratedValue
    private Long idTipoReacao;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "icone", nullable = false)
    private String icone;

    @Column(name = "desativacao")
    private LocalDateTime desativacao;

    public Long getIdTipoReacao() {
        return idTipoReacao;
    }

    public void setIdTipoReacao(Long idTipoReacao) {
        this.idTipoReacao = idTipoReacao;
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

    public LocalDateTime getDesativacao() {
        return desativacao;
    }

    public void setDesativacao(LocalDateTime desativacao) {
        this.desativacao = desativacao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TipoReacao that = (TipoReacao) o;
        return Objects.equals(idTipoReacao, that.idTipoReacao) && Objects.equals(nome, that.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTipoReacao, nome);
    }
}
