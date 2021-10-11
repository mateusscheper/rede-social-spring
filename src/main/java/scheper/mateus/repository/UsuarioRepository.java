package scheper.mateus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import scheper.mateus.dto.UsuarioDTO;
import scheper.mateus.entity.Usuario;

import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query(value = "SELECT new scheper.mateus.dto.UsuarioDTO(u.idUsuario, u.nome, f.caminho) " +
            "FROM Usuario u " +
            "LEFT JOIN u.foto f " +
            "WHERE u.idUsuario = :idUsuario")
    UsuarioDTO findUsuarioPorIdUsuario(@Param("idUsuario") Long idUsuario);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM api.usuario WHERE api.usuario.email = :email)", nativeQuery = true)
    boolean existsPorEmail(String email);
}
