package scheper.mateus.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import scheper.mateus.dto.ComentarioDTO;
import scheper.mateus.dto.NovoComentarioDTO;
import scheper.mateus.dto.NovoPostDTO;
import scheper.mateus.dto.PostDTO;
import scheper.mateus.dto.ReacaoDTO;
import scheper.mateus.entity.Arquivo;
import scheper.mateus.entity.Comentario;
import scheper.mateus.entity.Post;
import scheper.mateus.entity.Usuario;
import scheper.mateus.exception.UsuarioBusinessException;
import scheper.mateus.repository.ComentarioRepository;
import scheper.mateus.repository.PostRepository;
import scheper.mateus.repository.UsuarioRepository;
import scheper.mateus.utils.NumberUtils;

import javax.persistence.Transient;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static scheper.mateus.utils.ValidatorUtils.validarNulo;

@Service
public class PostService {

    private final PostRepository postRepository;

    private final UsuarioRepository usuarioRepository;

    private final ComentarioRepository comentarioRepository;

    private final HttpServletRequest request;

    public PostService(PostRepository postRepository, UsuarioRepository usuarioRepository, ComentarioRepository comentarioRepository, HttpServletRequest request) {
        this.postRepository = postRepository;
        this.usuarioRepository = usuarioRepository;
        this.comentarioRepository = comentarioRepository;
        this.request = request;
    }


    public List<PostDTO> findPostsByIdUsuario(Long idUsuario) {
        validarNulo(idUsuario, "ID de usuário inválido.");

        List<PostDTO> posts = new ArrayList<>();
        List<Object[]> dadosPosts = postRepository.findPostsByIdUsuario(idUsuario);

        for (Object[] dadosPost : dadosPosts)
            posts.add(new PostDTO(dadosPost));

        return posts;
    }

    @Transient
    public void save(String dadosPost, MultipartFile imagem) {
        NovoPostDTO novoPostDTO = obterNovoPostDTODoRequest(dadosPost);
        Usuario criador = obterUsuario(novoPostDTO.getIdUsuario());

        Post post = new Post();
        post.setCriador(criador);
        post.setCriacao(LocalDateTime.now());
        post.setDescricao(novoPostDTO.getDescricao());

        if (imagem != null && !imagem.isEmpty()) {
            File arquivoUpado = uparArquivoServidor(imagem);
            Arquivo arquivo = criarArquivo(arquivoUpado, criador);
            post.getArquivos().add(arquivo);
        }

        postRepository.save(post);
    }

    private File uparArquivoServidor(MultipartFile arquivo) {
        try {
            String pastaUpload = System.getProperty("user.home") + separator() + "uploads" + separator();

            if (!new File(pastaUpload).exists()) {
                new File(pastaUpload).mkdir();
            }

            String nomeArquivo = arquivo.getOriginalFilename();
            String caminhoCompletoArquivo = pastaUpload + nomeArquivo;
            File arquivoReal = new File(caminhoCompletoArquivo);
            arquivo.transferTo(arquivoReal);

            return arquivoReal;
        } catch (IOException e) {
            throw new UsuarioBusinessException("Arquivo inválido.");
        }
    }

    private String separator() {
        String separator = System.getProperty("file.separator");
        if (StringUtils.isEmpty(separator)) {
            return "/";
        }
        return separator;
    }

    private Arquivo criarArquivo(File imagem, Usuario usuario) {
        try {
            String nomeArquivo = imagem.getName();

            Arquivo arquivo = new Arquivo();
            arquivo.setDono(usuario);
            arquivo.setNome(nomeArquivo);
            arquivo.setCaminho(imagem.getCanonicalPath());
            arquivo.setTipo(nomeArquivo.substring(nomeArquivo.lastIndexOf(".") + 1));
            arquivo.setTamanho(Files.size(Path.of(imagem.getAbsolutePath())));

            return arquivo;
        } catch (IOException e) {
            throw new UsuarioBusinessException("Arquivo inválido.");
        }
    }

    private NovoPostDTO obterNovoPostDTODoRequest(String post) {
        if (StringUtils.isEmpty(post)) {
            throw new UsuarioBusinessException("Dados inválidos.");
        }

        ObjectMapper mapper = new ObjectMapper();
        NovoPostDTO novoPostDTO;
        try {
            novoPostDTO = mapper.readValue(post, NovoPostDTO.class);
        } catch (JsonProcessingException e) {
            throw new UsuarioBusinessException("Dados inválidos.");
        }

        validarNovoPost(novoPostDTO);

        return novoPostDTO;
    }

    private void validarNovoPost(NovoPostDTO novoPostDTO) {
        validarNulo(novoPostDTO.getIdUsuario(), "ID de usuário inválida.");
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
                .toList();
    }
}