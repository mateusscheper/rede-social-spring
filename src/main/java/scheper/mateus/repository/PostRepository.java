package scheper.mateus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import scheper.mateus.entity.Post;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query(value = "WITH posts AS (SELECT api.post.id_post, " +
            "                      api.post.descricao, " +
            "                      pa.caminho pa_caminho, " +
            "                      usuario.id_usuario, " +
            "                      usuario.nome, " +
            "                      COALESCE(api.arquivo.caminho, 'assets/nopic.png'), " +
            "                      api.post.criacao " +
            "               FROM api.post " +
            "                        LEFT JOIN api.post_arquivos ON post.id_post = post_arquivos.id_post " +
            "                        LEFT JOIN api.arquivo pa ON post_arquivos.id_arquivo = pa.id_arquivo " +
            "                        JOIN api.usuario_posts ON post.id_post = usuario_posts.id_post " +
            "                        JOIN api.usuario ON usuario_posts.id_usuario = usuario.id_usuario " +
            "                        LEFT JOIN api.arquivo ON usuario.id_arquivo_foto = arquivo.id_arquivo " +
            "                        JOIN api.usuario_amigos ON usuario.id_usuario = usuario_amigos.id_usuario_amigo " +
            "               WHERE api.usuario_amigos.id_usuario = :idUsuario " +
            "               UNION ALL " +
            "               SELECT api.post.id_post, " +
            "                      api.post.descricao, " +
            "                      pa.caminho pa_caminho, " +
            "                      usuario.id_usuario, " +
            "                      usuario.nome, " +
            "                      COALESCE(api.arquivo.caminho, 'assets/nopic.png'), " +
            "                      api.post.criacao " +
            "               FROM api.post " +
            "                        LEFT JOIN api.post_arquivos ON post.id_post = post_arquivos.id_post " +
            "                        LEFT JOIN api.arquivo pa ON post_arquivos.id_arquivo = pa.id_arquivo " +
            "                        JOIN api.usuario_posts ON post.id_post = usuario_posts.id_post " +
            "                        JOIN api.usuario ON usuario_posts.id_usuario = usuario.id_usuario " +
            "                        LEFT JOIN api.arquivo ON usuario.id_arquivo_foto = arquivo.id_arquivo " +
            "               WHERE api.usuario.id_usuario = :idUsuario) " +
            "SELECT * " +
            "FROM posts " +
            "ORDER BY criacao DESC",
            nativeQuery = true)
    List<Object[]> findPostsByIdUsuario(@Param("idUsuario") Long idUsuario);
}
