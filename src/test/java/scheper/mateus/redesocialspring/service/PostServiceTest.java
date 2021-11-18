package scheper.mateus.redesocialspring.service;

import org.easymock.EasyMockExtension;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import scheper.mateus.dto.PostCompletoDTO;
import scheper.mateus.entity.Arquivo;
import scheper.mateus.entity.Post;
import scheper.mateus.entity.Reacao;
import scheper.mateus.entity.Usuario;
import scheper.mateus.enums.ReacaoEnum;
import scheper.mateus.repository.ComentarioRepository;
import scheper.mateus.repository.PostRepository;
import scheper.mateus.repository.ReportRepository;
import scheper.mateus.repository.UsuarioRepository;
import scheper.mateus.service.ComentarioService;
import scheper.mateus.service.NotificacaoService;
import scheper.mateus.service.PostService;
import scheper.mateus.service.ReacaoService;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(EasyMockExtension.class)
class PostServiceTest extends EasyMockSupport {

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

    @Mock
    ReportRepository reportRepository;

    @BeforeEach
    void setup() {
        // TODO: Easymock ainda não tem suporte pro Java 17. Necessário adicionar "--add-opens java.base/java.lang=ALL-UNNAMED" como argumento ao rodar os testes
        postService = new PostService(postRepository, usuarioRepository, comentarioRepository, reacaoService, comentarioService, notificacaoService, reportRepository);
    }

    @Test
    void deveSalvarNovoPostComImagem() {
        String dadosPost = "{\"idUsuario\":148,\"descricao\":\"Teste\"}";
        Usuario criador = new Usuario();

        expect(usuarioRepository.findById(anyLong())).andReturn(Optional.of(criador));
        expect(reacaoService.obterReacoesAtivas()).andReturn(new ArrayList<>());
        expect(usuarioRepository.save(criador)).andReturn(criador);
        notificacaoService.criarNotificacao(anyObject(Usuario.class), anyObject(Usuario.class), anyString(), anyString());

        replayAll();
        Assertions.assertDoesNotThrow(() -> postService.save(dadosPost, null));
        verifyAll();
    }

    @Test
    void deveRetornarPostPorIdPost() {
        List<Post> posts = new ArrayList<>();
        LocalDateTime criacao = LocalDateTime.of(2021, 1, 1, 1, 1, 1);
        Post post = criarPostCompleto(posts, criacao);

        List<Object[]> dadosComentarios = new ArrayList<>();
        dadosComentarios.add(new Object[]{BigInteger.ONE, BigInteger.ONE, "Nome usuário", "caminho/foto.png", "Descrição", BigInteger.ONE});

        expect(postRepository.getById(1L)).andReturn(post);
        expect(reportRepository.existeReportAberto(post.getIdPost())).andReturn(false);
        expect(postRepository.findAllById(anyObject())).andReturn(posts);
        expect(comentarioRepository.findComentariosPorIdsPost(anyObject())).andReturn(dadosComentarios);
        comentarioService.popularReacoes(anyObject(), anyLong());
        comentarioService.popularSubcomentarios(anyObject(), anyLong());

        replayAll();
        PostCompletoDTO postCompletoDTO = postService.findPostCompletoPorIdPost(1L, 1L);
        verifyAll();

        assertEquals(1L, postCompletoDTO.getIdPost());
        assertEquals("Descrição", postCompletoDTO.getDescricao());
        assertEquals("caminho/arquivo.png", postCompletoDTO.getArquivo());
        assertEquals(1L, postCompletoDTO.getCriador().getIdUsuario());
        assertEquals("Nome usuário", postCompletoDTO.getCriador().getNome());
        assertEquals("caminho/crop-36x36.png", postCompletoDTO.getCriador().getFoto());
        assertEquals(criacao, postCompletoDTO.getCriacao());
        assertFalse(postCompletoDTO.isPossuiReport());
        assertEquals(1, postCompletoDTO.getReacoes().size());
        assertEquals(1L, postCompletoDTO.getReacoes().get(0).getIdReacao());
        assertEquals(ReacaoEnum.CURTIDA.getNome(), postCompletoDTO.getReacoes().get(0).getNome());
        assertEquals(ReacaoEnum.CURTIDA.getIcone(), postCompletoDTO.getReacoes().get(0).getIcone());
        assertEquals(0, postCompletoDTO.getReacoes().get(0).getQuantidade());
        assertFalse(postCompletoDTO.getReacoes().get(0).isMarcado());
        assertEquals(1, postCompletoDTO.getComentarios().size());
        assertEquals(1L, postCompletoDTO.getComentarios().get(0).getIdComentario());
        assertEquals(1L, postCompletoDTO.getComentarios().get(0).getIdUsuario());
        assertEquals("Nome usuário", postCompletoDTO.getComentarios().get(0).getNomeUsuario());
        assertEquals("caminho/foto.png", postCompletoDTO.getComentarios().get(0).getFotoUsuario());
        assertEquals("Descrição", postCompletoDTO.getComentarios().get(0).getDescricao());
    }

    private Post criarPostCompleto(List<Post> posts, LocalDateTime criacao) {
        Post post = new Post();
        post.setIdPost(1L);
        post.setDescricao("Descrição");

        Arquivo arquivo = new Arquivo();
        arquivo.setCaminho("caminho/arquivo.png");
        post.getArquivos().add(arquivo);

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNome("Nome usuário");
        Arquivo foto = new Arquivo();
        foto.setCaminhoCrop("caminho/crop-36x36.png");
        usuario.setFoto(foto);
        post.setUsuario(usuario);
        post.setCriacao(criacao);

        Reacao reacao = new Reacao();
        reacao.setIdReacao(1L);
        reacao.setTipo("Curtida");
        post.getReacoes().add(reacao);

        posts.add(post);
        return post;
    }

    @Test
    void deveRetornarPostsPorIdUsuario() {
        List<Object[]> dadosPost = new ArrayList<>();
        dadosPost.add(new Object[]{
                BigInteger.ONE,
                "Descrição",
                "/caminho/arquivo.png",
                BigInteger.ONE,
                "Nome usuário",
                "/caminho/foto/usuario.png",
                new Timestamp(new Date().getTime()),
                false});

        List<Post> postsConsulta = new ArrayList<>();
        Post post = new Post();
        post.setIdPost(1L);
        Reacao reacao = new Reacao();
        reacao.setIdReacao(1L);
        reacao.setTipo("Curtida");
        post.getReacoes().add(reacao);
        postsConsulta.add(post);

        expect(postRepository.findPostsPorIdUsuario(1L)).andReturn(dadosPost);
        expect(postRepository.findAllById(anyObject())).andReturn(postsConsulta);
        expect(comentarioRepository.findComentariosPorIdsPost(anyObject())).andReturn(new ArrayList<>());
        comentarioService.popularReacoes(anyObject(), anyLong());
        comentarioService.popularSubcomentarios(anyObject(), anyLong());

        replayAll();
        List<PostCompletoDTO> posts = postService.findPostsByIdUsuario(1L, false);
        verifyAll();

        assertEquals(1, posts.size());
        assertEquals(1L, posts.get(0).getIdPost());
        assertEquals("Descrição", posts.get(0).getDescricao());
        assertEquals("/caminho/arquivo.png", posts.get(0).getArquivo());
        assertEquals(1, posts.get(0).getReacoes().size());
        assertEquals(1L, posts.get(0).getReacoes().get(0).getIdReacao());
        assertEquals("Curtida", posts.get(0).getReacoes().get(0).getNome());
    }
}
