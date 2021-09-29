package scheper.mateus.service;

import org.springframework.stereotype.Service;
import scheper.mateus.dto.ReacaoDTO;
import scheper.mateus.dto.ReagirDTO;
import scheper.mateus.entity.Post;
import scheper.mateus.entity.Reacao;
import scheper.mateus.entity.Usuario;
import scheper.mateus.enums.ReacaoEnum;
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

    public ReacaoService(PostRepository postRepository, UsuarioService usuarioService, ReacaoRepository reacaoRepository) {
        this.postRepository = postRepository;
        this.usuarioService = usuarioService;
        this.reacaoRepository = reacaoRepository;
    }

    @SuppressWarnings("java:S2259")
    @Transactional
    public void reagir(ReagirDTO reagirDTO) {
        Long idPost = reagirDTO.getIdPost();
        Long idUsuario = reagirDTO.getIdUsuario();
        Long idReacao = reagirDTO.getIdReacao();

        validarReagir(idPost, idUsuario, idReacao);

        Post post = postRepository.getById(idPost);
        Reacao reacao = filtrarReacao(reagirDTO, post);

        if (isMarcacaoDiferente(reagirDTO.isMarcado(), reacao, idUsuario)) {
            Usuario usuario = usuarioService.findUsuarioById(idUsuario);
            if (reacaoPossuiUsuario(reacao, idUsuario)) {
                reacao.getUsuarios().remove(usuario);
            } else {
                if (!reacao.getUsuarios().contains(usuario))
                    reacao.getUsuarios().add(usuario);
                desmarcarOutrasReacoes(post, reagirDTO.getIdReacao(), idUsuario);
            }

            postRepository.save(post);
        }
    }

    private boolean isMarcacaoDiferente(boolean isUsuarioMarcando, Reacao reacao, Long idUsuario) {
        return reacaoPossuiUsuario(reacao, idUsuario) != isUsuarioMarcando;
    }

    private void validarReagir(Long idPost, Long idUsuario, Long idReacao) {
        validarNulo(idPost, "ID de post inválido.");
        validarNulo(idUsuario, "ID de usuário inválido.");
        validarNulo(idReacao, "ID de tipo inválido.");
    }

    private void desmarcarOutrasReacoes(Post post, Long idTipo, Long idUsuario) {
        List<Reacao> reacoesNaoMarcadas = filtrarReacoesParaDesmarcar(post, idTipo);

        for (Reacao reacao : reacoesNaoMarcadas) {
            reacao.getUsuarios().removeIf(u -> u.getIdUsuario().equals(idUsuario));
            reacaoRepository.save(reacao);
        }
    }

    private List<Reacao> filtrarReacoesParaDesmarcar(Post post, Long idTipo) {
        return post.getReacoes()
                .stream()
                .filter(r -> !r.getIdReacao().equals(idTipo))
                .toList();
    }

    private Reacao filtrarReacao(ReagirDTO reagirDTO, Post post) {
        return post.getReacoes()
                .stream()
                .filter(r -> r.getIdReacao().equals(reagirDTO.getIdReacao()))
                .findFirst()
                .orElse(null);
    }

    private boolean reacaoPossuiUsuario(Reacao reacao, Long idUsuario) {
        return reacao.getUsuarios()
                .stream()
                .anyMatch(u -> u.getIdUsuario().equals(idUsuario));
    }

    public List<ReacaoDTO> obterReacoesByIdPost(Long idPost, Long idUsuario) {
        List<ReacaoDTO> reacoesDTO = new ArrayList<>();
        validarNulo(idPost, "ID de post inválido.");

        Post post = postRepository.getById(idPost);
        validarNulo(post, "ID de post inválido.");

        for (Reacao reacao : post.getReacoes()) {
            ReacaoDTO reacaoDTO = new ReacaoDTO();
            reacaoDTO.setIdReacao(reacao.getIdReacao());
            reacaoDTO.setMarcado(reacaoPossuiUsuario(reacao, idUsuario));
            reacaoDTO.setNome(reacao.getTipo());
            reacaoDTO.setIcone(obterPathIcone(reacao.getTipo(), reacaoDTO.isMarcado()));
            reacaoDTO.setQuantidade(reacao.getUsuarios().size());
            reacoesDTO.add(reacaoDTO);
        }

        return reacoesDTO;
    }

    private String obterPathIcone(String nomeReacao, boolean isReacaoMarcada) {
        ReacaoEnum reacaoEnum = ReacaoEnum.parse(nomeReacao);
        if (reacaoEnum != null) {
            if (isReacaoMarcada)
                return reacaoEnum.getIconeMarcado();
            else
                return reacaoEnum.getIcone();
        }
        return null;
    }

    public List<Reacao> obterReacoesAtivas() {
        List<Reacao> reacoes = new ArrayList<>();
        for (ReacaoEnum reacaoEnum : ReacaoEnum.values())
            reacoes.add(new Reacao(reacaoEnum));
        return reacoes;
    }
}