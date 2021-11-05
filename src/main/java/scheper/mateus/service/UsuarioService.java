package scheper.mateus.service;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import scheper.mateus.dto.FotoPerfilDTO;
import scheper.mateus.dto.PostDTO;
import scheper.mateus.dto.UsuarioCompletoDTO;
import scheper.mateus.entity.Arquivo;
import scheper.mateus.entity.Post;
import scheper.mateus.entity.Usuario;
import scheper.mateus.enums.StatusAmizadeEnum;
import scheper.mateus.exception.BusinessException;
import scheper.mateus.repository.UsuarioRepository;
import scheper.mateus.utils.AnexoUtils;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static scheper.mateus.utils.ValidatorUtils.ID_DE_USUARIO_NAO_PODE_SER_NULO;
import static scheper.mateus.utils.ValidatorUtils.validarNulo;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public UsuarioService(UsuarioRepository usuarioRepository, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
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

    public UsuarioCompletoDTO findUsuarioPorIdUsuario(HttpServletRequest request, Long idUsuario) {
        validarNulo(ID_DE_USUARIO_NAO_PODE_SER_NULO, idUsuario);
        String token = obterTokenDoRequest(request);
        String email = obterEmailPeloToken(token);
        return obterUsuarioCompletoDTO(email, idUsuario);
    }

    public UsuarioCompletoDTO findUsuarioCompletoPorToken(HttpServletRequest request) {
        String token = obterTokenDoRequest(request);
        String email = obterEmailPeloToken(token);
        return obterUsuarioCompletoDTO(email, null);
    }

    private void popularPosts(UsuarioCompletoDTO usuarioDTO, Usuario usuarioLogado, Usuario usuarioDoPerfil) {
        List<Post> posts = usuarioDoPerfil != null ? usuarioDoPerfil.getPosts() : usuarioLogado.getPosts();
        usuarioDTO.setPosts(posts.stream().map(PostDTO::new).toList());
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

    private String obterEmailPeloToken(String token) {
        String email = jwtService.obterEmailByToken(token);
        if (ObjectUtils.isEmpty(email))
            throw new BusinessException("{usuario.validacao.naoEncontrado}");
        return email;
    }

    private String obterTokenDoRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (!token.startsWith("Bearer "))
            throw new BusinessException("usuario.validacao.naoEncontrado");
        return token.replace("Bearer ", "");
    }

    public void adicionarAmigo(HttpServletRequest request, Long idUsuario) {
        validarNulo(ID_DE_USUARIO_NAO_PODE_SER_NULO, idUsuario);
        String token = obterTokenDoRequest(request);
        String email = obterEmailPeloToken(token);

        Usuario usuarioLogado = usuarioRepository.findByEmail(email);
        usuarioLogado.getAmigos().add(usuarioRepository.getById(idUsuario));

        usuarioRepository.save(usuarioLogado);
    }

    public void desfazerAmizade(HttpServletRequest request, Long idUsuario) {
        validarNulo(ID_DE_USUARIO_NAO_PODE_SER_NULO, idUsuario);
        String token = obterTokenDoRequest(request);
        String email = obterEmailPeloToken(token);

        Usuario usuarioLogado = usuarioRepository.findByEmail(email);
        Usuario usuarioAmigo = usuarioRepository.getById(idUsuario);

        usuarioAmigo.getAmigos().removeIf(a -> a.getIdUsuario().equals(usuarioLogado.getIdUsuario()));
        usuarioRepository.save(usuarioLogado);

        usuarioLogado.getAmigos().removeIf(a -> a.getIdUsuario().equals(usuarioAmigo.getIdUsuario()));
        usuarioRepository.save(usuarioAmigo);
    }

    public void aceitarAmizade(HttpServletRequest request, Long idUsuario) {
        adicionarAmigo(request, idUsuario);
    }

    public void cancelarAdicionar(HttpServletRequest request, Long idUsuario) {
        validarNulo(ID_DE_USUARIO_NAO_PODE_SER_NULO, idUsuario);
        String token = obterTokenDoRequest(request);
        String email = obterEmailPeloToken(token);

        Usuario usuarioLogado = usuarioRepository.findByEmail(email);
        usuarioLogado.getAmigos().remove(usuarioRepository.getById(idUsuario));

        usuarioRepository.save(usuarioLogado);
    }

    public FotoPerfilDTO trocarFotoPerfil(Long idUsuario, MultipartFile imagem) {
        if (imagem == null || imagem.isEmpty())
            throw new BusinessException("{usuario.validacao.imagemVazia}");

        Usuario usuario = usuarioRepository.getById(idUsuario);

        Arquivo arquivo = AnexoUtils.uparImagemServidorComCropECriarArquivo(imagem, usuario, "usuario");

        if (usuario.getFoto() != null)
            AnexoUtils.excluirArquivoServidor(usuario.getFoto());

        usuario.setFoto(arquivo);

        usuarioRepository.save(usuario);

        return new FotoPerfilDTO(arquivo.getCaminho(), arquivo.getCaminhoCrop());
    }
}