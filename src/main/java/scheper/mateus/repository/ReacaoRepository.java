package scheper.mateus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import scheper.mateus.entity.Reacao;

@Repository
public interface ReacaoRepository extends JpaRepository<Reacao, Long> {
}
