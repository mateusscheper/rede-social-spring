package scheper.mateus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import scheper.mateus.entity.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query(value = "SELECT EXISTS(SELECT 1 " +
            "FROM api.report r " +
            "WHERE r.comentario_id_comentario = :idPostOuComentario " +
            "OR r.post_id_post = :idPostOuComentario)", nativeQuery = true)
    boolean existeReportAberto(Long idPostOuComentario);
}
