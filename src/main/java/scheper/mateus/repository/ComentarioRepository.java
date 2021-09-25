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
            "       tipo_reacao.nome, " +
            "       tipo_reacao.icone, " +
            "       usuario.id_usuario, " +
            "       usuario.nome AS nome_usuario " +
            "FROM api.comentario_reacoes " +
            "         JOIN api.reacao ON comentario_reacoes.id_reacao = reacao.id_reacao " +
            "         JOIN api.tipo_reacao ON reacao.tipo_id_tipo_reacao = tipo_reacao.id_tipo_reacao " +
            "         JOIN api.usuario ON reacao.usuario_id_usuario = usuario.id_usuario " +
            "WHERE comentario_reacoes.id_comentario IN (:idsComentarios) " +
            "ORDER BY api.tipo_reacao.nome, api.usuario.nome", nativeQuery = true)
    List<Object[]> findReacoesByIdsComentarios(List<Long> idsComentarios);
}
