package scheper.mateus.dto;

import scheper.mateus.entity.Usuario;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UsuarioCompletoDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long idUsuario;

    private String nome;

    private String email;

    private LocalDate dataNascimento;

    private Integer quantidadeAmigos;

    private String foto;

    private String statusAmizade;

    private List<PostDTO> posts = new ArrayList<>();

    public UsuarioCompletoDTO(Usuario usuario) {
        this.idUsuario = usuario.getIdUsuario();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.dataNascimento = usuario.getDataNascimento();
        this.quantidadeAmigos = usuario.getQuantidadeAmigos();
        this.foto = usuario.getFoto() != null ? usuario.getFoto().getCaminho() : "assets/nopic.png";
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

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Integer getQuantidadeAmigos() {
        return quantidadeAmigos;
    }

    public void setQuantidadeAmigos(Integer quantidadeAmigos) {
        this.quantidadeAmigos = quantidadeAmigos;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getStatusAmizade() {
        return statusAmizade;
    }

    public void setStatusAmizade(String statusAmizade) {
        this.statusAmizade = statusAmizade;
    }

    public List<PostDTO> getPosts() {
        return posts;
    }

    public void setPosts(List<PostDTO> posts) {
        this.posts = posts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioCompletoDTO that = (UsuarioCompletoDTO) o;
        return Objects.equals(idUsuario, that.idUsuario) && Objects.equals(nome, that.nome) && Objects.equals(email, that.email) && Objects.equals(dataNascimento, that.dataNascimento) && Objects.equals(quantidadeAmigos, that.quantidadeAmigos) && Objects.equals(foto, that.foto) && Objects.equals(posts, that.posts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, nome, email, dataNascimento, quantidadeAmigos, foto, posts);
    }
}