package scheper.mateus.redesocialspring.service;

import org.easymock.EasyMockSupport;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import scheper.mateus.dto.NovoUsuarioDTO;
import scheper.mateus.entity.Usuario;
import scheper.mateus.repository.UsuarioRepository;
import scheper.mateus.service.UsuarioService;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.*;

@SpringBootTest
class UsuarioServiceTest extends EasyMockSupport {

    @Mock
    private UsuarioRepository usuarioRepository;

    private final UsuarioService usuarioService = new UsuarioService(usuarioRepository);

    @Test
    void deveSalvarUsuario() {
        NovoUsuarioDTO novoUsuarioDTO = new NovoUsuarioDTO();
        novoUsuarioDTO.setNome("Mateus Scheper");
        novoUsuarioDTO.setEmail("mateus_scheper@hotmail.com");
        novoUsuarioDTO.setDataNascimento(LocalDate.of(1994, 12, 27));

        Usuario usuario = new Usuario(novoUsuarioDTO);
        usuario.setIdUsuario(1L);
        expect(usuarioRepository.save(usuario)).andReturn(usuario);

        replayAll();
        usuarioService.save(novoUsuarioDTO);
        verifyAll();
    }

}
