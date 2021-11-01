package scheper.mateus.service;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import scheper.mateus.dto.PostDTO;
import scheper.mateus.dto.UsuarioCompletoDTO;
import scheper.mateus.entity.Usuario;
import scheper.mateus.exception.BusinessException;
import scheper.mateus.repository.UsuarioRepository;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static scheper.mateus.utils.ValidatorUtils.validarNulo;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PostService postService;

    public UsuarioService(UsuarioRepository usuarioRepository, JwtService jwtService, PostService postService) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.postService = postService;
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

    public UsuarioCompletoDTO findUsuarioPorIdUsuario(Long idUsuario) {
        validarNulo("ID de usuário não pode ser nulo.", idUsuario);
        return obterUsuarioCompletoDTO(null, idUsuario);
    }

    public UsuarioCompletoDTO findUsuarioCompletoPorToken(HttpServletRequest request) {
        String token = obterTokenDoRequest(request);
        String email = obterEmailPeloToken(token);
        return obterUsuarioCompletoDTO(email, null);
    }

    private void popularPosts(UsuarioCompletoDTO usuarioDTO) {
        List<PostDTO> posts = postService.findPostsByIdUsuario(usuarioDTO.getIdUsuario());
        usuarioDTO.setPosts(posts);
    }

    private UsuarioCompletoDTO obterUsuarioCompletoDTO(String email, Long idUsuario) {
        Usuario usuario = idUsuario != null ? usuarioRepository.getById(idUsuario) : usuarioRepository.findByEmail(email);
        UsuarioCompletoDTO usuarioDTO = new UsuarioCompletoDTO(usuario);
        popularPosts(usuarioDTO);
        return usuarioDTO;
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
}