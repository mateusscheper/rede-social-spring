package scheper.mateus.service;

import org.springframework.stereotype.Service;
import scheper.mateus.dto.ComentarioDTO;
import scheper.mateus.dto.NovoComentarioDTO;
import scheper.mateus.dto.ReacaoDTO;
import scheper.mateus.entity.Comentario;
import scheper.mateus.entity.Post;
import scheper.mateus.entity.Reacao;
import scheper.mateus.entity.Usuario;
import scheper.mateus.exception.UsuarioBusinessException;
import scheper.mateus.repository.ComentarioRepository;
import scheper.mateus.repository.PostRepository;
import scheper.mateus.repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static scheper.mateus.utils.ValidatorUtils.validarNulo;

@Service
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;

    private final UsuarioRepository usuarioRepository;

    private final PostRepository postRepository;

    private final ReacaoService reacaoService;

    public ComentarioService(ComentarioRepository comentarioRepository, UsuarioRepository usuarioRepository, PostRepository postRepository, ReacaoService reacaoService) {
        this.comentarioRepository = comentarioRepository;
        this.usuarioRepository = usuarioRepository;
        this.postRepository = postRepository;
        this.reacaoService = reacaoService;
    }

    public void saveComentario(NovoComentarioDTO novoComentarioDTO) {
        Comentario comentario = new Comentario();
        comentario.setCriador(obterUsuario(novoComentarioDTO.getIdUsuario()));
        comentario.setPost(obterPost(novoComentarioDTO.getIdPost()));
        comentario.setDescricao(novoComentarioDTO.getDescricao());
        comentario.setCriacao(LocalDateTime.now());
        comentario.setReacoes(reacaoService.obterReacoesAtivas());
        comentarioRepository.save(comentario);
    }

    public List<ComentarioDTO> findComentariosByIdPost(Long idPost, Long idUsuario, Integer limit) {
        List<ComentarioDTO> comentariosDTO = new ArrayList<>();
        validarNulo("ID de post inválido.", idPost);

        List<Object[]> dadosComentarios = comentarioRepository.findComentariosByIdPost(idPost, limit);
        dadosComentarios.forEach(dados -> comentariosDTO.add(new ComentarioDTO(dados)));

        if (!comentariosDTO.isEmpty())
            comentariosDTO.get(0).setQuantidadeComentariosPost(comentarioRepository.countComentariosByPost(idPost));

        popularReacoes(comentariosDTO, idUsuario);

        return comentariosDTO;
    }

    private Post obterPost(Long idPost) {
        return postRepository.findById(idPost).orElseThrow(() -> new UsuarioBusinessException("Post não encontrado."));
    }

    private void popularReacoes(List<ComentarioDTO> comentariosDTO, Long idUsuario) {
        for (ComentarioDTO comentarioDTO : comentariosDTO) {
            Comentario comentario = comentarioRepository.getById(comentarioDTO.getIdComentario());
            for (Reacao reacao : comentario.getReacoes()) {
                ReacaoDTO reacaoDTO = new ReacaoDTO(reacao, idUsuario);
                comentarioDTO.getReacoes().add(reacaoDTO);
            }
        }
    }

    private Usuario obterUsuario(Long idUsuario) {
        return usuarioRepository.findById(idUsuario).orElseThrow(() -> new UsuarioBusinessException("Usuário não encontrado."));
    }
}