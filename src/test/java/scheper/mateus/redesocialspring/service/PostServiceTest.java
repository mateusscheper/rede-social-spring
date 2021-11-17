package scheper.mateus.redesocialspring.service;

import org.easymock.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import scheper.mateus.repository.ComentarioRepository;
import scheper.mateus.repository.PostRepository;
import scheper.mateus.repository.UsuarioRepository;
import scheper.mateus.service.ComentarioService;
import scheper.mateus.service.NotificacaoService;
import scheper.mateus.service.PostService;
import scheper.mateus.service.ReacaoService;

import java.util.ArrayList;

import static org.easymock.EasyMock.*;

@ExtendWith(EasyMockExtension.class)
class PostServiceTest extends EasyMockSupport {

    @TestSubject
    private PostService postService;

    @Mock
    PostRepository postRepository;

    @Mock
    UsuarioRepository usuarioRepository;

    @Mock
    ComentarioRepository comentarioRepository;

    @Mock
    ReacaoService reacaoService;

    @Mock
    ComentarioService comentarioService;

    @Mock
    NotificacaoService notificacaoService;

    @BeforeEach
    void setup() {
        // TODO: Easymock ainda não tem suporte pro Java 17
        postService = new PostService(postRepository, usuarioRepository,
                comentarioRepository, reacaoService, comentarioService, notificacaoService, null);
    }

    @Test
    void test() {
        postService.findPostsByIdUsuario(1L, false);
        expect(postRepository.findPostsPorIdUsuario(1L)).andReturn(new ArrayList<>());
    }
}
