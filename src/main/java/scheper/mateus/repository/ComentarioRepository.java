package scheper.mateus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import scheper.mateus.entity.Comentario;

import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    @Query(value = "SELECT c.id_comentario, " +
            "       u.id_usuario, " +
            "       u.nome, " +
            "       COALESCE(a.caminho, 'assets/nopic.png'), " +
            "       c.descricao " +
            "FROM api.comentario c " +
            "         JOIN api.usuario u ON c.id_usuario_criador = u.id_usuario " +
            "         LEFT JOIN api.arquivo a ON u.id_arquivo_foto = a.id_arquivo " +
            "WHERE c.id_post = :idPost " +
            "ORDER BY c.criacao DESC " +
            "LIMIT :limit", nativeQuery = true)
    List<Object[]> findComentariosByIdPost(@Param("idPost") Long idPost, @Param("limit") Integer limit);

    @Query(value = "SELECT count(c) " +
            "FROM api.comentario c " +
            "WHERE c.id_post = :idPost", nativeQuery = true)
    int countComentariosByPost(@Param("idPost") Long idPost);

    @Query(value = "SELECT comentario_reacoes.id_comentario, " +
            "       reacao.tipo, " +
            "       usuario.id_usuario, " +
            "       usuario.nome AS nome_usuario " +
            "FROM api.comentario_reacoes " +
            "         JOIN api.reacao ON comentario_reacoes.id_reacao = reacao.id_reacao " +
            "         JOIN api.reacao_usuarios ON reacao.id_reacao = reacao_usuarios.id_reacao " +
            "         JOIN api.usuario ON reacao_usuarios.id_usuario = usuario.id_usuario " +
            "WHERE comentario_reacoes.id_comentario IN (:idsComentarios) " +
            "ORDER BY api.reacao.tipo, api.usuario.nome", nativeQuery = true)
    List<Object[]> findReacoesByIdsComentarios(List<Long> idsComentarios);
}
