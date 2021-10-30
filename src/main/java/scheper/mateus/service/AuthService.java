package scheper.mateus.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import scheper.mateus.dto.LoginRegistroDTO;
import scheper.mateus.dto.NovoUsuarioDTO;
import scheper.mateus.dto.LoginDTO;
import scheper.mateus.entity.Usuario;
import scheper.mateus.exception.BusinessException;
import scheper.mateus.repository.UsuarioRepository;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public void registrar(NovoUsuarioDTO novoUsuarioDTO) {
        if (usuarioRepository.existsPorEmail(novoUsuarioDTO.getEmail()))
            throw new BusinessException("{usuario.validacao.emailJaCadastrado}");

        novoUsuarioDTO.setSenha(passwordEncoder.encode(novoUsuarioDTO.getSenha()));
        Usuario usuario = new Usuario(novoUsuarioDTO);
        
        usuarioRepository.save(usuario);
    }

    public LoginDTO login(LoginRegistroDTO loginDTO) {
        Usuario usuario = usuarioRepository.findByEmail(loginDTO.getEmail());
        if (usuario == null)
            throw new BusinessException("{usuario.validacao.emailOuSenhaInvalidos}");

        if (senhaCorreta(loginDTO, usuario))
            return jwtService.gerarToken(usuario);
        else
            throw new BusinessException("{usuario.validacao.emailOuSenhaInvalidos}");
    }

    private boolean senhaCorreta(LoginRegistroDTO loginDTO, Usuario usuario) {
        return passwordEncoder.matches(loginDTO.getSenha(), usuario.getSenha());
    }

    public void validarToken(String token) {
        if (!jwtService.isTokenValido(token))
            throw new BusinessException("{usuario.validacao.tokenInvalido}");
    }
}