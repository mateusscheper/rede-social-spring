package scheper.mateus.service;

import org.springframework.stereotype.Service;
import scheper.mateus.dto.ComentarioDTO;
import scheper.mateus.dto.NovoComentarioDTO;
import scheper.mateus.dto.NovoPostDTO;
import scheper.mateus.dto.PostDTO;
import scheper.mateus.dto.ReacaoDTO;
import scheper.mateus.entity.Comentario;
import scheper.mateus.entity.Post;
import scheper.mateus.entity.Usuario;
import scheper.mateus.exception.UsuarioBusinessException;
import scheper.mateus.repository.ComentarioRepository;
import scheper.mateus.repository.PostRepository;
import scheper.mateus.repository.UsuarioRepository;
import scheper.mateus.utils.NumberUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static scheper.mateus.utils.ValidatorUtils.validarNulo;

@Service
public class PostService {

    private final PostRepository postRepository;

    private final UsuarioRepository usuarioRepository;

    private final ComentarioRepository comentarioRepository;

    public PostService(PostRepository postRepository, UsuarioRepository usuarioRepository, ComentarioRepository comentarioRepository) {
        this.postRepository = postRepository;
        this.usuarioRepository = usuarioRepository;
        this.comentarioRepository = comentarioRepository;
    }


    public List<PostDTO> findPostsByIdUsuario(Long idUsuario) {
        validarNulo(idUsuario, "ID de usuário inválido.");

        List<PostDTO> posts = new ArrayList<>();
        List<Object[]> dadosPosts = postRepository.findPostsByIdUsuario(idUsuario);

        for (Object[] dadosPost : dadosPosts)
            posts.add(new PostDTO(dadosPost));

        return posts;
    }

    public void save(NovoPostDTO novoPostDTO) {
        Post post = new Post();
        post.setCriador(obterUsuario(novoPostDTO.getIdUsuario()));
        post.setCriacao(LocalDateTime.now());
        post.setDescricao(novoPostDTO.getDescricao());

        postRepository.save(post);
    }

    private Usuario obterUsuario(Long idUsuario) {
        return usuarioRepository.findById(idUsuario).orElseThrow(() -> new UsuarioBusinessException("Usuário não encontrado."));
    }

    private Post obterPost(Long idPost) {
        return postRepository.findById(idPost).orElseThrow(() -> new UsuarioBusinessException("Post não encontrado."));
    }

    public void saveComentario(NovoComentarioDTO novoComentarioDTO) {
        Comentario comentario = new Comentario();
        comentario.setCriador(obterUsuario(novoComentarioDTO.getIdUsuario()));
        comentario.setPost(obterPost(novoComentarioDTO.getIdPost()));
        comentario.setDescricao(novoComentarioDTO.getDescricao());
        comentario.setCriacao(LocalDateTime.now());

        comentarioRepository.save(comentario);
    }

    public List<ComentarioDTO> findComentariosByIdPost(Long idPost) {
        validarNulo(idPost, "ID de post inválido.");
        List<ComentarioDTO> comentariosDTO = comentarioRepository.findComentariosByIdPost(idPost);
        popularReacoes(comentariosDTO);
        return comentariosDTO;
    }

    private void popularReacoes(List<ComentarioDTO> comentariosDTO) {
        if (comentariosDTO.isEmpty())
            return;

        List<Long> idsComentarios = mapearIdsComentarios(comentariosDTO);

        List<Object[]> dadosReacoes = comentarioRepository.findReacoesByIdsComentarios(idsComentarios);

        for (Object[] dados : dadosReacoes) {
            Long idComentario = NumberUtils.castBigIntegerToLong(dados[0]);
            ComentarioDTO comentarioDTO = filtrarComentario(comentariosDTO, idComentario);
            if (comentarioDTO != null) {
                comentarioDTO.getReacoes().add(new ReacaoDTO(dados));
            }
        }
    }

    private ComentarioDTO filtrarComentario(List<ComentarioDTO> comentariosDTO, Long idComentario) {
        return comentariosDTO
                .stream()
                .filter(c -> c.getIdComentario().equals(idComentario))
                .findFirst()
                .orElse(null);
    }

    private List<Long> mapearIdsComentarios(List<ComentarioDTO> comentariosDTO) {
        return comentariosDTO
                .stream()
                .map(ComentarioDTO::getIdComentario)
                .collect(Collectors.toList());
    }
}