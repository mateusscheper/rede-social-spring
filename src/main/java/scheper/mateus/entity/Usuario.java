package scheper.mateus.entity;

import scheper.mateus.dto.NovoUsuarioDTO;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(schema = "api", name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Email(message = "E-mail inválido")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "senha", nullable = false)
    private String senha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_arquivo_foto")
    private Arquivo foto;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "data_bloqueio")
    private LocalDateTime dataBloqueio;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "usuario_amigos",
            schema = "api",
            joinColumns = {
                    @JoinColumn(name = "id_usuario")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "id_usuario_amigo")
            })
    private List<Usuario> amigos = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "usuario_posts",
            schema = "api",
            joinColumns = {
                    @JoinColumn(name = "id_usuario")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "id_post")
            })
    private List<Post> posts = new ArrayList<>();

    public Usuario() {
    }

    public Usuario(NovoUsuarioDTO novoUsuarioDTO) {
        this.nome = novoUsuarioDTO.getNome().trim();
        this.email = novoUsuarioDTO.getEmail().toLowerCase().trim();
        this.senha = novoUsuarioDTO.getSenha();
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

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Arquivo getFoto() {
        return foto;
    }

    public void setFoto(Arquivo foto) {
        this.foto = foto;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public LocalDateTime getDataBloqueio() {
        return dataBloqueio;
    }

    public void setDataBloqueio(LocalDateTime dataBloqueio) {
        this.dataBloqueio = dataBloqueio;
    }

    public List<Usuario> getAmigos() {
        return amigos;
    }

    public void setAmigos(List<Usuario> amigos) {
        this.amigos = amigos;
    }

    public int getQuantidadeAmigos() {
        int qtde = 0;

        for (Usuario amigo : this.amigos) {
            if (amigo.getAmigos()
                    .stream()
                    .anyMatch(a -> a.getIdUsuario().equals(this.idUsuario)))
                qtde++;
        }

        return qtde;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> postagens) {
        this.posts = postagens;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(idUsuario, usuario.idUsuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario);
    }
}
