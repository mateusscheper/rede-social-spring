package scheper.mateus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import scheper.mateus.entity.Notificacao;

import java.util.List;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    @Query(value = "SELECT n " +
            "FROM Notificacao n " +
            "WHERE n.criador.idUsuario = :idUsuarioCriador " +
            "AND n.recebedor.idUsuario = :idUsuarioRecebedor")
    List<Notificacao> obterNotificacaoPorUsuarios(Long idUsuarioCriador, Long idUsuarioRecebedor);

    @Query(value = "SELECT n.* " +
            "FROM api.notificacao n " +
            "WHERE (n.id_usuario_criador IN (:idsUsuariosCriadores) AND n.id_usuario_recebedor IS NULL) " +
            "   OR (n.id_usuario_criador IN (:idsUsuariosCriadores) AND n.id_usuario_recebedor = :idUsuarioRecebedor) " +
            " ORDER BY n.horario DESC " +
            " LIMIT 20", nativeQuery = true)
    List<Notificacao> consultarNotificacoesRecentes(List<Long> idsUsuariosCriadores, Long idUsuarioRecebedor);
}