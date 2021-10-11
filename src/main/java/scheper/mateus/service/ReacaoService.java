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

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static scheper.mateus.utils.ValidatorUtils.validarNulo;

@Service
public class ReacaoService {

    private final PostRepository postRepository;

    private final UsuarioService usuarioService;

    private final ReacaoRepository reacaoRepository;

    private final ComentarioRepository comentarioRepository;

    public ReacaoService(PostRepository postRepository, UsuarioService usuarioService, ReacaoRepository reacaoRepository, ComentarioRepository comentarioRepository) {
        this.postRepository = postRepository;
        this.usuarioService = usuarioService;
        this.reacaoRepository = reacaoRepository;
        this.comentarioRepository = comentarioRepository;
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
        if (idComentario == null) {
            post = postRepository.getById(idPost);
            reacao = filtrarReacao(reagirDTO, post, null);
        } else {
            comentario = comentarioRepository.getById(idComentario);
            reacao = filtrarReacao(reagirDTO, null, comentario);
        }

        if (isMarcacaoDiferente(reagirDTO.isMarcado(), reacao, idUsuario)) {
            Usuario usuario = usuarioService.findUsuarioPorIdUsuario(idUsuario);
            if (reacaoPossuiUsuario(reacao, idUsuario)) {
                reacao.getUsuarios().removeIf(u -> u.getIdUsuario().equals(idUsuario));
            } else {
                if (!reacao.getUsuarios().contains(usuario))
                    reacao.getUsuarios().add(usuario);
                desmarcarOutrasReacoes(post, comentario, reagirDTO.getIdReacao(), idUsuario);
            }

            reacaoRepository.save(reacao);

            if (comentario == null)
                postRepository.save(post);
            else
                comentarioRepository.save(comentario);
        }
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
            reacao.getUsuarios().removeIf(u -> u.getIdUsuario().equals(idUsuario));
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
}