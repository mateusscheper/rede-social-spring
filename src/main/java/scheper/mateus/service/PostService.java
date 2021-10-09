package scheper.mateus.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import scheper.mateus.dto.ComentarioDTO;
import scheper.mateus.dto.NovoPostDTO;
import scheper.mateus.dto.PostCompletoDTO;
import scheper.mateus.dto.PostDTO;
import scheper.mateus.dto.UsuarioDTO;
import scheper.mateus.entity.Arquivo;
import scheper.mateus.entity.Post;
import scheper.mateus.entity.Usuario;
import scheper.mateus.exception.BusinessException;
import scheper.mateus.repository.ComentarioRepository;
import scheper.mateus.repository.PostRepository;
import scheper.mateus.repository.UsuarioRepository;

import javax.persistence.Transient;
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

    private final ReacaoService reacaoService;

    private final ComentarioService comentarioService;

    public PostService(PostRepository postRepository, UsuarioRepository usuarioRepository, ComentarioRepository comentarioRepository, ReacaoService reacaoService, ComentarioService comentarioService) {
        this.postRepository = postRepository;
        this.usuarioRepository = usuarioRepository;
        this.comentarioRepository = comentarioRepository;
        this.reacaoService = reacaoService;
        this.comentarioService = comentarioService;
    }


    public List<PostDTO> findPostsByIdUsuario(Long idUsuario) {
        validarNulo("ID de usuário inválido.", idUsuario);

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
        post.setUsuario(criador);
        post.setCriacao(LocalDateTime.now());
        post.setDescricao(novoPostDTO.getDescricao());
        post.setReacoes(reacaoService.obterReacoesAtivas());

        if (imagem != null && !imagem.isEmpty()) {
            String nomeArquivoCodificado = getNomeArquivoCodificado(imagem, criador);
            String nomeReal = imagem.getOriginalFilename() != null ? imagem.getOriginalFilename() : imagem.getName();

            File arquivoUpado = uparArquivoServidor(imagem, nomeArquivoCodificado, criador.getIdUsuario());
            Arquivo arquivo = criarArquivo(arquivoUpado, criador, nomeReal, nomeArquivoCodificado);

            post.getArquivos().add(arquivo);
        }

        criador.getPosts().add(post);
        usuarioRepository.save(criador);
    }

    private String getNomeArquivoCodificado(MultipartFile imagem, Usuario criador) {
        String nome =  codificarNome(criador.getIdUsuario()) + ".";

        if (imagem.getOriginalFilename() != null)
            return nome.concat(imagem.getOriginalFilename().substring(imagem.getOriginalFilename().lastIndexOf(".") + 1));
        else
            return nome.concat(imagem.getName().substring(imagem.getName().lastIndexOf(".") + 1));
    }

    private File uparArquivoServidor(MultipartFile arquivo, String nomeArquivoCodificado, Long idUsuario) {
        try {
            String pastaUpload = System.getProperty("user.home") + separator() + "uploads" + separator() + "post" + separator();

            if (!new File(pastaUpload).exists()) {
                new File(pastaUpload).mkdirs();
            }

            File arquivoReal = criarArquivoFisico(nomeArquivoCodificado, pastaUpload, idUsuario);

            arquivo.transferTo(arquivoReal);

            return arquivoReal;
        } catch (IOException e) {
            throw new BusinessException("Arquivo inválido.");
        }
    }

    private File criarArquivoFisico(String nomeArquivoCodificado, String pastaUpload, Long idUsuario) {
        String caminhoCompletoArquivo = pastaUpload + nomeArquivoCodificado;
        File arquivoReal = new File(caminhoCompletoArquivo);
        if (arquivoReal.exists()) {
            nomeArquivoCodificado = codificarNome(idUsuario);
            criarArquivoFisico(nomeArquivoCodificado, pastaUpload, idUsuario);
        }
        return arquivoReal;
    }

    private String separator() {
        String separator = System.getProperty("file.separator");
        if (StringUtils.isEmpty(separator)) {
            return "/";
        }
        return separator;
    }

    private Arquivo criarArquivo(File imagem, Usuario usuario, String nomeReal, String nomeCodificado) {
        try {
            Arquivo arquivo = new Arquivo();
            arquivo.setDono(usuario);
            arquivo.setNomeReal(nomeReal);
            arquivo.setNomeCodificado(nomeCodificado);
            arquivo.setCaminho(obterCaminhoUpload(nomeCodificado));
            arquivo.setTipo(nomeReal.substring(nomeReal.lastIndexOf(".") + 1));
            arquivo.setTamanho(Files.size(Path.of(imagem.getAbsolutePath())));

            return arquivo;
        } catch (IOException e) {
            throw new BusinessException("Arquivo inválido.");
        }
    }

    private String obterCaminhoUpload(String nomeCodificado) {
        return "assets" + separator() + "uploads" + separator() + "post" + separator() + nomeCodificado;
    }

    private String codificarNome(Long idUsuario) {
        return idUsuario.toString() + RandomStringUtils.randomAlphanumeric(30);
    }

    private NovoPostDTO obterNovoPostDTODoRequest(String post) {
        if (StringUtils.isEmpty(post)) {
            throw new BusinessException("Dados inválidos.");
        }

        ObjectMapper mapper = new ObjectMapper();
        NovoPostDTO novoPostDTO;
        try {
            novoPostDTO = mapper.readValue(post, NovoPostDTO.class);
        } catch (JsonProcessingException e) {
            throw new BusinessException("Dados inválidos.");
        }

        validarNovoPost(novoPostDTO);

        return novoPostDTO;
    }

    private void validarNovoPost(NovoPostDTO novoPostDTO) {
        validarNulo("ID de usuário inválida.", novoPostDTO.getIdUsuario());
    }

    private Usuario obterUsuario(Long idUsuario) {
        return usuarioRepository.findById(idUsuario).orElseThrow(() -> new BusinessException("Usuário não encontrado."));
    }

    public PostCompletoDTO findPostById(Long idPost, Long idUsuario) {
        validarNulo("ID de post inválido.", idPost);
        validarNulo("ID de usuário inválido.", idUsuario);

        Post post = postRepository.findById(idPost).orElseThrow(() -> new BusinessException("Post não encontrado."));

        PostCompletoDTO postDTO = new PostCompletoDTO();
        postDTO.setIdPost(post.getIdPost());
        postDTO.setCriacao(post.getCriacao());
        postDTO.setCriador(new UsuarioDTO(post.getUsuario()));
        postDTO.setDescricao(post.getDescricao());
        postDTO.setArquivo(!post.getArquivos().isEmpty() ? post.getArquivos().get(0).getCaminho() : null);

        List<Object[]> dadosComentarios = comentarioRepository.findComentariosByIdPost(idPost, 100);
        dadosComentarios.forEach(dados -> postDTO.getComentarios().add(new ComentarioDTO(dados)));

        comentarioService.popularReacoes(postDTO.getComentarios(), idUsuario);

        return postDTO;
    }
}