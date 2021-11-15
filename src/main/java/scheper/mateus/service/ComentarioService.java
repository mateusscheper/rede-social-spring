package scheper.mateus.service;

import org.springframework.stereotype.Service;
import scheper.mateus.dto.ComentarioDTO;
import scheper.mateus.dto.NovoComentarioDTO;
import scheper.mateus.dto.ReacaoDTO;
import scheper.mateus.entity.Comentario;
import scheper.mateus.entity.Post;
import scheper.mateus.entity.Reacao;
import scheper.mateus.entity.Usuario;
import scheper.mateus.exception.BusinessException;
import scheper.mateus.repository.ComentarioRepository;
import scheper.mateus.repository.PostRepository;
import scheper.mateus.repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static scheper.mateus.utils.NumberUtils.castBigIntegerToLong;
import static scheper.mateus.utils.ValidatorUtils.validarNulo;

@Service
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;

    private final UsuarioRepository usuarioRepository;

    private final PostRepository postRepository;

    private final ReacaoService reacaoService;

    private final NotificacaoService notificacaoService;

    public ComentarioService(ComentarioRepository comentarioRepository, UsuarioRepository usuarioRepository, PostRepository postRepository, ReacaoService reacaoService, NotificacaoService notificacaoService) {
        this.comentarioRepository = comentarioRepository;
        this.usuarioRepository = usuarioRepository;
        this.postRepository = postRepository;
        this.reacaoService = reacaoService;
        this.notificacaoService = notificacaoService;
    }

    public void save(NovoComentarioDTO novoComentarioDTO) {
        validarNulo("Necessário informar um ID de post ou um ID de comentário.", novoComentarioDTO.getIdPost(), novoComentarioDTO.getIdComentario());

        Comentario comentario = new Comentario();
        comentario.setCriador(obterUsuario(novoComentarioDTO.getIdUsuario()));

        Post post = null;
        Comentario comentarioPai = null;
        if (novoComentarioDTO.getIdPost() != null) {
            post = obterPost(novoComentarioDTO.getIdPost());
            comentario.setPost(post);
        } else if (novoComentarioDTO.getIdComentario() != null) {
            comentarioPai = comentarioRepository.getById(novoComentarioDTO.getIdComentario());
            comentario.setComentarioPai(comentarioPai);
        }

        comentario.setDescricao(novoComentarioDTO.getDescricao());
        comentario.setCriacao(LocalDateTime.now());
        comentario.setReacoes(reacaoService.obterReacoesAtivas());
        comentarioRepository.save(comentario);
        notificarDonoPostOuComentarioPai(post, comentarioPai, novoComentarioDTO.getIdUsuario());
    }

    private void notificarDonoPostOuComentarioPai(Post post, Comentario comentarioPai, Long idUsuario) {
        Usuario usuarioCriador = obterUsuarioCriador(post, comentarioPai);
        Usuario usuarioLogado = obterUsuario(idUsuario);
        criarNotificacao(post, comentarioPai, usuarioCriador, usuarioLogado);
    }

    private void criarNotificacao(Post post, Comentario comentarioPai, Usuario usuarioCriador, Usuario usuarioLogado) {
        notificacaoService.criarNotificacao(usuarioLogado, usuarioCriador, obterMensagemConteudo(post != null, usuarioLogado), obterLinkComentario(post, comentarioPai));
    }

    private Usuario obterUsuarioCriador(Post post, Comentario comentarioPai) {
        if (post != null)
            return post.getUsuario();
        else if (comentarioPai != null)
            return comentarioPai.getCriador();
        return null;
    }

    private String obterLinkComentario(Post post, Comentario comentarioPai) {
        if (post != null)
            return "/post/" + post.getIdPost();
        else if (comentarioPai != null && comentarioPai.getPost() != null)
            return "/post/" + comentarioPai.getPost().getIdPost();
        else
            return null;
    }

    private String obterMensagemConteudo(boolean isPost, Usuario usuarioLogado) {
        return usuarioLogado.getNome() + (isPost ? " comentou em seu post." : " respondeu seu comentário.");
    }

    public List<ComentarioDTO> findComentariosPorIdPost(Long idPost, Long idUsuario, Integer limit) {
        List<ComentarioDTO> comentariosDTO = new ArrayList<>();
        validarNulo("ID de post inválido.", idPost);

        List<Object[]> dadosComentarios = comentarioRepository.findComentariosPorIdPost(idPost, limit);
        dadosComentarios.forEach(dados -> comentariosDTO.add(new ComentarioDTO(dados)));

        if (!comentariosDTO.isEmpty())
            comentariosDTO.get(0).setQuantidadeComentariosPost(comentarioRepository.countComentariosPorIdPost(idPost));

        popularReacoes(comentariosDTO, idUsuario);
        popularSubcomentarios(comentariosDTO, idUsuario);

        return comentariosDTO;
    }

    public void popularSubcomentarios(List<ComentarioDTO> comentariosDTO, Long idUsuario) {
        List<Long> idsComentarios = comentariosDTO
                .stream()
                .distinct()
                .map(ComentarioDTO::getIdComentario)
                .toList();

        if (idsComentarios.isEmpty())
            return;

        List<Object[]> dadosSubcomentarios = comentarioRepository.findSubcomentariosPorIdsComentario(idsComentarios);
        popularDadosSubcomentarios(comentariosDTO, dadosSubcomentarios);

        List<ComentarioDTO> subcomentarios = new ArrayList<>();
        comentariosDTO.forEach(c -> subcomentarios.addAll(c.getSubcomentarios()));
        popularReacoes(subcomentarios, idUsuario);
    }

    private void popularDadosSubcomentarios(List<ComentarioDTO> comentariosDTO, List<Object[]> dadosSubcomentarios) {
        for (Object[] dadosSubcomentario : dadosSubcomentarios) {
            Long idComentarioPai = castBigIntegerToLong(dadosSubcomentario[5]);

            comentariosDTO
                    .stream()
                    .filter(c -> c.getIdComentario().equals(idComentarioPai))
                    .findFirst()
                    .ifPresent(comentarioPai -> comentarioPai.getSubcomentarios().add(new ComentarioDTO(dadosSubcomentario)));
        }
    }

    private Post obterPost(Long idPost) {
        return postRepository.findById(idPost).orElseThrow(() -> new BusinessException("Post não encontrado."));
    }

    public void popularReacoes(List<ComentarioDTO> comentariosDTO, Long idUsuario) {
        for (ComentarioDTO comentarioDTO : comentariosDTO) {
            Comentario comentario = comentarioRepository.getById(comentarioDTO.getIdComentario());
            for (Reacao reacao : comentario.getReacoes()) {
                ReacaoDTO reacaoDTO = new ReacaoDTO(reacao, idUsuario);
                comentarioDTO.getReacoes().add(reacaoDTO);
            }
        }
    }

    private Usuario obterUsuario(Long idUsuario) {
        return usuarioRepository.findById(idUsuario).orElseThrow(() -> new BusinessException("Usuário não encontrado."));
    }

    public List<ComentarioDTO> findSubcomentariosPorIdComentario(Long idComentarioPai, Long idUsuario) {
        List<ComentarioDTO> subcomentarios = new ArrayList<>();
        List<Object[]> dadosSubcomentarios = comentarioRepository.findSubcomentariosPorIdsComentario(Collections.singletonList(idComentarioPai));
        dadosSubcomentarios.forEach(dados -> subcomentarios.add(new ComentarioDTO(dados)));
        popularReacoes(subcomentarios, idUsuario);
        return subcomentarios;
    }
}