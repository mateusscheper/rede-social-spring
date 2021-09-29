package scheper.mateus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import scheper.mateus.dto.ComentarioDTO;
import scheper.mateus.entity.Comentario;

import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    @Query(value = "SELECT new scheper.mateus.dto.ComentarioDTO(c.idComentario, " +
            "c.criador.idUsuario, " +
            "c.criador.nome, " +
            "COALESCE(f.caminho, 'assets/nopic.png'), " +
            "c.descricao) " +
            "FROM Comentario c " +
            "LEFT JOIN c.criador.foto f " +
            "WHERE c.post.idPost = :idPost")
    List<ComentarioDTO> findComentariosByIdPost(@Param("idPost") Long idPost);


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
