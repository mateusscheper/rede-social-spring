package scheper.mateus.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import scheper.mateus.dto.ComentarioDTO;
import scheper.mateus.dto.NovoPostDTO;
import scheper.mateus.dto.PostCompletoDTO;
import scheper.mateus.dto.ReacaoDTO;
import scheper.mateus.entity.Arquivo;
import scheper.mateus.entity.Post;
import scheper.mateus.entity.Usuario;
import scheper.mateus.exception.BusinessException;
import scheper.mateus.repository.ComentarioRepository;
import scheper.mateus.repository.PostRepository;
import scheper.mateus.repository.ReportRepository;
import scheper.mateus.repository.UsuarioRepository;
import scheper.mateus.utils.AnexoUtils;

import javax.persistence.Transient;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static scheper.mateus.utils.NumberUtils.castBigIntegerToLong;
import static scheper.mateus.utils.ValidatorUtils.validarNulo;

@Service
public class PostService {

    private final PostRepository postRepository;

    private final UsuarioRepository usuarioRepository;

    private final ComentarioRepository comentarioRepository;

    private final ReacaoService reacaoService;

    private final ComentarioService comentarioService;

    private final NotificacaoService notificacaoService;

    private final ReportRepository reportRepository;

    public PostService(PostRepository postRepository, UsuarioRepository usuarioRepository, ComentarioRepository comentarioRepository, ReacaoService reacaoService, ComentarioService comentarioService, NotificacaoService notificacaoService, ReportRepository reportRepository) {
        this.postRepository = postRepository;
        this.usuarioRepository = usuarioRepository;
        this.comentarioRepository = comentarioRepository;
        this.reacaoService = reacaoService;
        this.comentarioService = comentarioService;
        this.notificacaoService = notificacaoService;
        this.reportRepository = reportRepository;
    }

    @Transient
    public void save(String dadosPost, MultipartFile imagem) {
        NovoPostDTO novoPostDTO = obterNovoPostDTODoRequest(dadosPost);
        Usuario criador = obterUsuario(novoPostDTO.getIdUsuario());
        Post post = popularDadosPost(novoPostDTO, criador);
        uparEAdicionarArquivoAoPost(imagem, criador, post);
        criador.getPosts().add(post);
        usuarioRepository.save(criador);
        notificarNovoPost(criador, post.getIdPost());
    }

    public List<PostCompletoDTO> findPostsByIdUsuario(Long idUsuario, boolean mostrarPostsAmigos) {
        validarNulo("ID de usuário inválido.", idUsuario);
        List<PostCompletoDTO> posts = obterPostsDTO(idUsuario, mostrarPostsAmigos);
        popularComentariosEReacoes(idUsuario, posts);
        return posts;
    }

    public PostCompletoDTO findPostCompletoPorIdPost(Long idPost, Long idUsuario) {
        validarNulo("ID de usuário ou post inválido.", idPost, idUsuario);

        Post post = postRepository.getById(idPost);
        PostCompletoDTO postCompletoDTO = new PostCompletoDTO(post);
        postCompletoDTO.setPossuiReport(reportRepository.existeReportAberto(post.getIdPost()));
        popularComentariosEReacoes(idUsuario, Collections.singletonList(postCompletoDTO));

        return postCompletoDTO;
    }

    private void popularComentariosEReacoes(Long idUsuario, List<PostCompletoDTO> posts) {
        popularReacoes(posts, idUsuario);
        popularComentarios(posts);
        popularReacoesComentarios(posts, idUsuario);
        popularSubcomentarios(posts, idUsuario);
    }

    private List<PostCompletoDTO> obterPostsDTO(Long idUsuario, boolean mostrarPostsAmigos) {
        List<PostCompletoDTO> posts = new ArrayList<>();
        List<Object[]> dadosPosts = obterDadosPosts(idUsuario, mostrarPostsAmigos);

        dadosPosts.forEach(dados -> posts.add(new PostCompletoDTO(dados)));
        return posts;
    }

    private List<Object[]> obterDadosPosts(Long idUsuario, boolean mostrarPostsAmigos) {
        List<Object[]> dadosPosts;
        if (mostrarPostsAmigos)
            dadosPosts = postRepository.findPostsPorIdUsuarioComAmigos(idUsuario);
        else
            dadosPosts = postRepository.findPostsPorIdUsuario(idUsuario);
        return dadosPosts;
    }

    private void popularReacoes(List<PostCompletoDTO> postsDTO, Long idUsuario) {
        List<Long> idsPost = postsDTO
                .stream()
                .map(PostCompletoDTO::getIdPost)
                .toList();

        if (idsPost.isEmpty())
            return;

        List<Post> posts = postRepository.findAllById(idsPost);
        if (posts.isEmpty())
            return;

        for (Post post : posts) {
            postsDTO
                    .stream()
                    .filter(postDTO -> postDTO.getIdPost().equals(post.getIdPost()))
                    .findFirst()
                    .ifPresent(postDTO -> popularReacoesPost(postDTO, post, idUsuario));
        }
    }

    private void popularReacoesPost(PostCompletoDTO postDTO, Post post, Long idUsuario) {
        post.getReacoes().forEach(reacao -> postDTO.getReacoes().add(new ReacaoDTO(reacao, idUsuario)));
    }

    private void popularSubcomentarios(List<PostCompletoDTO> posts, Long idUsuario) {
        List<ComentarioDTO> comentarios = new ArrayList<>();
        posts.forEach(p -> comentarios.addAll(p.getComentarios()));

        comentarioService.popularSubcomentarios(comentarios, idUsuario);
    }

    private void popularComentarios(List<PostCompletoDTO> posts) {
        List<Long> idsPost = posts
                .stream()
                .map(PostCompletoDTO::getIdPost)
                .toList();

        if (idsPost.isEmpty())
            return;

        List<Object[]> dadosComentarios = comentarioRepository.findComentariosPorIdsPost(idsPost);

        for (Object[] dadosComentario : dadosComentarios) {
            Long idPost = castBigIntegerToLong(dadosComentario[5]);

            posts.stream()
                    .filter(post -> post.getIdPost().equals(idPost))
                    .findFirst()
                    .ifPresent(post -> post.getComentarios().add(new ComentarioDTO(dadosComentario)));
        }
    }

    private void notificarNovoPost(Usuario criador, Long idPost) {
        notificacaoService.criarNotificacao(criador, null, criador.getNome() + " acabou de postar.", "/post/" + idPost);
    }

    private void uparEAdicionarArquivoAoPost(MultipartFile imagem, Usuario criador, Post post) {
        if (imagem != null && !imagem.isEmpty()) {
            Arquivo arquivo = AnexoUtils.uparImagemServidorComCropECriarArquivo(imagem, criador, "post");
            post.getArquivos().add(arquivo);
        }
    }

    private Post popularDadosPost(NovoPostDTO novoPostDTO, Usuario criador) {
        Post post = new Post();
        post.setUsuario(criador);
        post.setCriacao(LocalDateTime.now());
        post.setDescricao(novoPostDTO.getDescricao());
        post.setReacoes(reacaoService.obterReacoesAtivas());
        return post;
    }

    private NovoPostDTO obterNovoPostDTODoRequest(String post) {
        if (StringUtils.isEmpty(post))
            throw new BusinessException("Dados inválidos.");
        NovoPostDTO novoPostDTO = popularNovoPostDTO(post);
        validarNulo("ID de usuário inválida.", novoPostDTO.getIdUsuario());
        return novoPostDTO;
    }

    private NovoPostDTO popularNovoPostDTO(String post) {
        ObjectMapper mapper = new ObjectMapper();
        NovoPostDTO novoPostDTO;
        try {
            novoPostDTO = mapper.readValue(post, NovoPostDTO.class);
        } catch (JsonProcessingException e) {
            throw new BusinessException("Dados inválidos.");
        }
        return novoPostDTO;
    }

    private Usuario obterUsuario(Long idUsuario) {
        return usuarioRepository.findById(idUsuario).orElseThrow(() -> new BusinessException("Usuário não encontrado."));
    }

    private void popularReacoesComentarios(List<PostCompletoDTO> posts, Long idUsuario) {
        List<ComentarioDTO> comentarios = new ArrayList<>();
        posts.forEach(postDTO -> comentarios.addAll(postDTO.getComentarios()));

        comentarioService.popularReacoes(comentarios, idUsuario);
    }
}