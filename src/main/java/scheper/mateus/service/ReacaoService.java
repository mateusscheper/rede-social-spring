package scheper.mateus.service;

import org.springframework.stereotype.Service;
import scheper.mateus.dto.ReacaoDTO;
import scheper.mateus.dto.ReagirDTO;
import scheper.mateus.entity.Comentario;
import scheper.mateus.entity.Post;
import scheper.mateus.entity.Reacao;
import scheper.mateus.entity.Usuario;
import scheper.mateus.enums.ReacaoEnum;
import scheper.mateus.repository.ComentarioRepository;
import scheper.mateus.repository.PostRepository;
import scheper.mateus.repository.ReacaoRepository;
import scheper.mateus.repository.UsuarioRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static scheper.mateus.utils.ValidatorUtils.validarNulo;

@Service
public class ReacaoService {

    private final PostRepository postRepository;

    private final ReacaoRepository reacaoRepository;

    private final ComentarioRepository comentarioRepository;

    private final UsuarioRepository usuarioRepository;

    private final NotificacaoService notificacaoService;

    public ReacaoService(PostRepository postRepository, ReacaoRepository reacaoRepository, ComentarioRepository comentarioRepository, UsuarioRepository usuarioRepository, NotificacaoService notificacaoService) {
        this.postRepository = postRepository;
        this.reacaoRepository = reacaoRepository;
        this.comentarioRepository = comentarioRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacaoService = notificacaoService;
    }

    @Transactional
    public void reagir(ReagirDTO reagirDTO) {
        Long idPost = reagirDTO.getIdPost();
        Long idComentario = reagirDTO.getIdComentario();
        Long idUsuario = reagirDTO.getIdUsuario();
        Long idReacao = reagirDTO.getIdReacao();

        validarReagir(idPost, idComentario, idUsuario, idReacao);

        Post post = null;
        Comentario comentario = null;
        Reacao reacao;
        if (isReacaoEmPost(idComentario)) {
            post = postRepository.getById(idPost);
            reacao = filtrarReacao(reagirDTO, post, null);
        } else {
            comentario = comentarioRepository.getById(idComentario);
            reacao = filtrarReacao(reagirDTO, null, comentario);
        }

        if (isMarcacaoDiferente(reagirDTO.isMarcado(), reacao, idUsuario)) {
            Usuario usuario = usuarioRepository.getById(idUsuario);

            if (reacaoPossuiUsuario(reacao, idUsuario)) {
                desmarcarReacao(idUsuario, reacao);
                excluirNotificacoes(idUsuario, post, comentario);
            } else {
                if (naoPossuiReacao(reacao, usuario)) {
                    marcarReacao(reacao, usuario);
                    notificarReacao(post, comentario, usuario, reacao.getIdReacao());
                }
                desmarcarOutrasReacoes(post, comentario, reagirDTO.getIdReacao(), idUsuario);
            }

            reacaoRepository.save(reacao);

            salvarPostOuComentarioReagido(post, comentario);
        }
    }

    private void excluirNotificacoes(Long idUsuario, Post post, Comentario comentario) {
        Usuario usuarioDoPostOuComentario = post != null ? post.getUsuario() : comentario.getCriador();
        if (usuarioDoPostOuComentario != null)
            notificacaoService.removerNotificacaoAdicionar(idUsuario, usuarioDoPostOuComentario.getIdUsuario());
    }

    private void notificarReacao(Post post, Comentario comentario, Usuario usuarioLogado, Long idReacao) {
        if (isPostOuComentarioDeUsuarioDiferente(post, comentario, usuarioLogado.getIdUsuario())
                && !postOuComentarioPossuiReacaoUsuario(post, comentario, usuarioLogado.getIdUsuario(), idReacao)) {
            notificacaoService.criarNotificacao(usuarioLogado,
                    obterUsuarioNotificarReacao(post, comentario),
                    obterMensagemReacao(post, comentario, usuarioLogado),
                    obterLinkPost(post, comentario));
        }
    }

    private boolean postOuComentarioPossuiReacaoUsuario(Post post, Comentario comentario, Long idUsuario, Long idReacao) {
        List<Reacao> reacoes = post != null ? post.getReacoes() : comentario.getReacoes();

        return reacoes
                .stream()
                .anyMatch(r -> !r.getIdReacao().equals(idReacao)
                        && r.getUsuarios()
                        .stream()
                        .anyMatch(u -> u.getIdUsuario().equals(idUsuario)));
    }

    public List<ReacaoDTO> findReacoesPorIdPostOuIdComentario(Long idPost, Long idComentario, Long idUsuario) {
        List<ReacaoDTO> reacoesDTO = new ArrayList<>();
        validarNulo("ID de post inválido.", idPost, idComentario);

        Post post;
        Comentario comentario;
        List<Reacao> reacoes;

        if (idComentario == null) {
            post = postRepository.getById(idPost);
            reacoes = post.getReacoes();
        } else {
            comentario = comentarioRepository.getById(idComentario);
            reacoes = comentario.getReacoes();
        }

        for (Reacao reacao : reacoes) {
            ReacaoDTO reacaoDTO = new ReacaoDTO(reacao, idUsuario);
            reacoesDTO.add(reacaoDTO);
        }

        return reacoesDTO;
    }

    public List<Reacao> obterReacoesAtivas() {
        List<Reacao> reacoes = new ArrayList<>();
        for (ReacaoEnum reacaoEnum : ReacaoEnum.values())
            reacoes.add(new Reacao(reacaoEnum));
        return reacoes;
    }

    private boolean isPostOuComentarioDeUsuarioDiferente(Post post, Comentario comentario, Long idUsuario) {
        Usuario usuarioDoPostOuComentario = post != null ? post.getUsuario() : comentario.getCriador();
        return usuarioDoPostOuComentario != null && !usuarioDoPostOuComentario.getIdUsuario().equals(idUsuario);
    }

    private String obterMensagemReacao(Post post, Comentario comentario, Usuario usuarioLogado) {
        String conteudo = usuarioLogado.getNome() + " reagiu ao seu ";
        if (post != null)
            return conteudo + "post.";
        else if (comentario != null)
            return conteudo + "comentário.";
        return null;
    }

    private String obterLinkPost(Post post, Comentario comentario) {
        String url = "/post/";

        if (post != null)
            return url + post.getIdPost();
        else if (comentario != null && comentario.getPost() != null)
            return url + comentario.getPost().getIdPost();
        else if (comentario != null && comentario.getComentarioPai() != null && comentario.getComentarioPai().getPost() != null)
            return url + comentario.getComentarioPai().getPost().getIdPost();

        return null;
    }

    private Usuario obterUsuarioNotificarReacao(Post post, Comentario comentario) {
        if (post != null)
            return post.getUsuario();
        else if (comentario != null)
            return comentario.getCriador();
        return null;
    }

    private void salvarPostOuComentarioReagido(Post post, Comentario comentario) {
        if (comentario == null)
            postRepository.save(post);
        else
            comentarioRepository.save(comentario);
    }

    private void marcarReacao(Reacao reacao, Usuario usuario) {
        reacao.getUsuarios().add(usuario);
    }

    private boolean naoPossuiReacao(Reacao reacao, Usuario usuario) {
        return !reacao.getUsuarios().contains(usuario);
    }

    private boolean desmarcarReacao(Long idUsuario, Reacao reacao) {
        return reacao.getUsuarios().removeIf(u -> u.getIdUsuario().equals(idUsuario));
    }

    private boolean isReacaoEmPost(Long idComentario) {
        return idComentario == null;
    }

    private boolean isMarcacaoDiferente(boolean isUsuarioMarcando, Reacao reacao, Long idUsuario) {
        return reacaoPossuiUsuario(reacao, idUsuario) != isUsuarioMarcando;
    }

    private void validarReagir(Long idPost, Long idComentario, Long idUsuario, Long idReacao) {
        validarNulo("ID de post e comentário inválidos.", idPost, idComentario);
        validarNulo("ID de usuário inválido.", idUsuario);
        validarNulo("ID de tipo inválido.", idReacao);
    }

    private void desmarcarOutrasReacoes(Post post, Comentario comentario, Long idTipo, Long idUsuario) {
        List<Reacao> reacoesNaoMarcadas = filtrarReacoesParaDesmarcar(post, comentario, idTipo);
        for (Reacao reacao : reacoesNaoMarcadas) {
            desmarcarReacao(idUsuario, reacao);
            reacaoRepository.save(reacao);
        }
    }

    private List<Reacao> filtrarReacoesParaDesmarcar(Post post, Comentario comentario, Long idTipo) {
        if (comentario == null) {
            return post.getReacoes()
                    .stream()
                    .filter(r -> !r.getIdReacao().equals(idTipo))
                    .toList();
        } else {
            return comentario.getReacoes()
                    .stream()
                    .filter(r -> !r.getIdReacao().equals(idTipo))
                    .toList();
        }
    }

    private Reacao filtrarReacao(ReagirDTO reagirDTO, Post post, Comentario comentario) {
        if (comentario == null) {
            return post.getReacoes()
                    .stream()
                    .filter(r -> r.getIdReacao().equals(reagirDTO.getIdReacao()))
                    .findFirst()
                    .orElse(null);
        } else {
            return comentario.getReacoes()
                    .stream()
                    .filter(r -> r.getIdReacao().equals(reagirDTO.getIdReacao()))
                    .findFirst()
                    .orElse(null);
        }
    }

    private boolean reacaoPossuiUsuario(Reacao reacao, Long idUsuario) {
        return reacao.getUsuarios()
                .stream()
                .anyMatch(u -> u.getIdUsuario().equals(idUsuario));
    }
}