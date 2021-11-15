package scheper.mateus.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import scheper.mateus.dto.*;
import scheper.mateus.entity.Arquivo;
import scheper.mateus.entity.Post;
import scheper.mateus.entity.Usuario;
import scheper.mateus.enums.StatusAmizadeEnum;
import scheper.mateus.exception.BusinessException;
import scheper.mateus.repository.UsuarioRepository;
import scheper.mateus.utils.AnexoUtils;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static scheper.mateus.utils.AnexoUtils.uparImagemServidorComCropECriarArquivo;
import static scheper.mateus.utils.ValidatorUtils.ID_DE_USUARIO_NAO_PODE_SER_NULO;
import static scheper.mateus.utils.ValidatorUtils.validarNulo;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    private final JwtService jwtService;

    private final NotificacaoService notificacaoService;

    private final PostService postService;

    public UsuarioService(UsuarioRepository usuarioRepository, JwtService jwtService, NotificacaoService notificacaoService, PostService postService) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.notificacaoService = notificacaoService;
        this.postService = postService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null)
            throw new UsernameNotFoundException("Usuário não encontrado.");

        return User
                .builder()
                .username(usuario.getEmail())
                .password(usuario.getSenha())
                .roles("USER")
                .build();
    }

    public UsuarioCompletoDTO findUsuarioCompletoPorIdUsuario(HttpServletRequest request, Long idUsuario) {
        validarNulo(ID_DE_USUARIO_NAO_PODE_SER_NULO, idUsuario);
        String email = obterEmailUsuarioDoRequest(request);
        return obterUsuarioCompletoDTO(email, idUsuario);
    }

    public UsuarioCompletoDTO findUsuarioCompletoPorToken(HttpServletRequest request) {
        String email = obterEmailUsuarioDoRequest(request);
        return obterUsuarioCompletoDTO(email, null);
    }

    private String obterEmailUsuarioDoRequest(HttpServletRequest request) {
       return jwtService.obterEmailDoRequest(request);
    }

    public UsuarioCompletoDTO findUsuarioCompletoComFotosPorIdUsuario(Long idUsuario) {
        Usuario usuario = usuarioRepository.getById(idUsuario);
        validarNulo("{usuario.validacao.naoEncontrado}", usuario);

        UsuarioCompletoDTO usuarioDTO = new UsuarioCompletoDTO(usuario);
        for (Post post : usuario.getPosts()) {
            if (!post.getArquivos().isEmpty())
                post.getArquivos().forEach(arquivo -> usuarioDTO.getFotos().add(arquivo.getCaminho()));
        }

        return usuarioDTO;
    }

    public UsuarioCompletoDTO findUsuarioCompletoComAmigosPorIdUsuario(Long idUsuario) {
        Usuario usuario = usuarioRepository.getById(idUsuario);
        validarNulo("{usuario.validacao.naoEncontrado}", usuario);

        UsuarioCompletoDTO usuarioDTO = new UsuarioCompletoDTO(usuario);
        usuario.getAmigos().forEach(amigo -> {
            if (isAdicionado(amigo, usuario.getIdUsuario()))
                usuarioDTO.getAmigos().add(new UsuarioSimplesDTO(amigo, true));
        });

        return usuarioDTO;
    }

    public void adicionarAmigo(HttpServletRequest request, Long idUsuario) {
        validarNulo(ID_DE_USUARIO_NAO_PODE_SER_NULO, idUsuario);
        String email = obterEmailUsuarioDoRequest(request);

        Usuario usuarioLogado = usuarioRepository.findByEmail(email);
        usuarioLogado.getAmigos().add(usuarioRepository.getById(idUsuario));

        usuarioRepository.save(usuarioLogado);

        criarNotificacaoAmizade(idUsuario, usuarioLogado);
    }

    public void desfazerAmizade(HttpServletRequest request, Long idUsuario) {
        validarNulo(ID_DE_USUARIO_NAO_PODE_SER_NULO, idUsuario);
        String email = obterEmailUsuarioDoRequest(request);

        Usuario usuarioLogado = usuarioRepository.findByEmail(email);
        Usuario usuarioAmigo = usuarioRepository.getById(idUsuario);

        usuarioAmigo.getAmigos().removeIf(a -> a.getIdUsuario().equals(usuarioLogado.getIdUsuario()));
        usuarioRepository.save(usuarioLogado);

        usuarioLogado.getAmigos().removeIf(a -> a.getIdUsuario().equals(usuarioAmigo.getIdUsuario()));
        usuarioRepository.save(usuarioAmigo);
    }

    public FotoPerfilDTO trocarFotoPerfil(Long idUsuario, MultipartFile imagem) {
        if (imagem == null || imagem.isEmpty())
            throw new BusinessException("{usuario.validacao.imagemVazia}");

        Usuario usuario = usuarioRepository.getById(idUsuario);

        Arquivo arquivo = uparImagemServidorComCropECriarArquivo(imagem, usuario, "usuario");

        if (usuario.getFoto() != null)
            AnexoUtils.excluirArquivoServidor(usuario.getFoto());

        usuario.setFoto(arquivo);

        usuarioRepository.save(usuario);

        criarNotificacao(usuario);

        return new FotoPerfilDTO(arquivo.getCaminho(), arquivo.getCaminhoCrop());
    }

    private void criarNotificacao(Usuario usuario) {
        notificacaoService.criarNotificacao(usuario, null, obterMensagemNotificacao(usuario, " trocou a foto de perfil."), obterLinkPerfilUsuario(usuario.getIdUsuario()));
    }

    private String obterLinkPerfilUsuario(Long idUsuario) {
        return "/perfil/" + idUsuario;
    }

    private String obterMensagemNotificacao(Usuario usuario, String conteudoNotificacao) {
        return usuario.getNome() + conteudoNotificacao;
    }

    public List<UsuarioSimplesDTO> buscarUsuarioPorNomeOuEmail(String query) {
        validarNulo("{buscaUsuario.validacao.vazio}", query);

        List<Usuario> resultado = usuarioRepository.buscarUsuarioPorNomeOuEmail(query);

        return resultado
                .stream()
                .map(u -> new UsuarioSimplesDTO(u, false))
                .toList();
    }

    public void aceitarAmizade(HttpServletRequest request, Long idUsuario) {
        adicionarAmigo(request, idUsuario);
    }

    public void cancelarAdicionar(HttpServletRequest request, Long idUsuario) {
        validarNulo(ID_DE_USUARIO_NAO_PODE_SER_NULO, idUsuario);
        String email = obterEmailUsuarioDoRequest(request);

        Usuario usuarioLogado = usuarioRepository.findByEmail(email);
        usuarioLogado.getAmigos().remove(usuarioRepository.getById(idUsuario));

        usuarioRepository.save(usuarioLogado);

        removerNotificacaoAdicionar(usuarioLogado.getIdUsuario(), idUsuario);
    }

    private boolean isAdicionado(Usuario amigo, Long idUsuario) {
        return amigo.getAmigos()
                .stream()
                .anyMatch(u -> u.getIdUsuario().equals(idUsuario));
    }

    private void popularPosts(UsuarioCompletoDTO usuarioDTO, Usuario usuarioLogado, Usuario usuarioDoPerfil) {
        Long idUsuario = usuarioDoPerfil != null ? usuarioDoPerfil.getIdUsuario() : usuarioLogado.getIdUsuario();

        List<PostCompletoDTO> posts = postService.findPostsByIdUsuario(idUsuario, false);
        usuarioDTO.setPosts(posts
                .stream()
                .sorted((o1, o2) -> {
                    if (o1.getCriacao() == null || o2.getCriacao() == null)
                        return 0;
                    return o2.getCriacao().compareTo(o1.getCriacao());
                })
                .toList());
    }

    private UsuarioCompletoDTO obterUsuarioCompletoDTO(String email, Long idUsuario) {
        Usuario usuarioDoPerfil;
        Usuario usuarioLogado = null;

        if (idUsuario != null) {
            usuarioDoPerfil = usuarioRepository.getById(idUsuario);
            usuarioLogado = usuarioRepository.findByEmail(email);
        } else {
            usuarioDoPerfil = usuarioRepository.findByEmail(email);
        }

        UsuarioCompletoDTO usuarioDTO = new UsuarioCompletoDTO(usuarioDoPerfil);
        popularStatusAmizade(usuarioDTO, usuarioLogado, usuarioDoPerfil);
        popularPosts(usuarioDTO, usuarioLogado, usuarioDoPerfil);
        return usuarioDTO;
    }

    private void popularStatusAmizade(UsuarioCompletoDTO usuarioDTO, Usuario usuarioLogado, Usuario usuarioDoPerfil) {
        if (usuarioLogado == null) {
            usuarioDTO.setStatusAmizade(null);
            return;
        }

        boolean isUmLadoAdicionado = usuarioLogado.getAmigos()
                .stream()
                .anyMatch(amigo -> amigo.getIdUsuario().equals(usuarioDoPerfil.getIdUsuario()));

        boolean isOutroLadoAdicionado = usuarioDoPerfil.getAmigos()
                .stream()
                .anyMatch(amigo -> amigo.getIdUsuario().equals(usuarioLogado.getIdUsuario()));

        if (isUmLadoAdicionado && isOutroLadoAdicionado)
            usuarioDTO.setStatusAmizade(StatusAmizadeEnum.AMIGOS.getStatus());
        else if (isUmLadoAdicionado)
            usuarioDTO.setStatusAmizade(StatusAmizadeEnum.PENDENTE_RESPOSTA.getStatus());
        else if (isOutroLadoAdicionado)
            usuarioDTO.setStatusAmizade(StatusAmizadeEnum.PENDENTE_ACEITE.getStatus());
    }

    private void criarNotificacaoAmizade(Long idUsuarioAdicionado, Usuario usuarioLogado) {
        Usuario usuarioAdicionado = usuarioRepository.getById(idUsuarioAdicionado);

        if (isNovoPedidoAmizade(usuarioLogado.getIdUsuario(), usuarioAdicionado))
            notificarPedidoAmizade(usuarioLogado, usuarioAdicionado, obterMensagemNotificacao(usuarioLogado, " enviou a você um pedido de amizade."));
        else
            notificarPedidoAmizade(usuarioLogado, usuarioAdicionado, obterMensagemNotificacao(usuarioLogado, " aceitou seu pedido de amizade."));
    }

    private void notificarPedidoAmizade(Usuario usuarioLogado, Usuario usuarioAdicionado, String conteudo) {
        notificacaoService.criarNotificacao(usuarioLogado, usuarioAdicionado, conteudo, obterLinkPerfilUsuario(usuarioLogado.getIdUsuario()));
    }

    private boolean isNovoPedidoAmizade(Long idUsuarioAdicionador, Usuario usuarioAdicionado) {
        return isAdicionado(usuarioAdicionado, idUsuarioAdicionador);
    }

    private void removerNotificacaoAdicionar(Long idUsuarioLogado, Long idUsuarioAmigo) {
        notificacaoService.removerNotificacaoAdicionar(idUsuarioLogado, idUsuarioAmigo);
    }
}