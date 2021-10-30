package scheper.mateus.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import scheper.mateus.dto.UsuarioDTO;
import scheper.mateus.entity.Usuario;
import scheper.mateus.repository.UsuarioRepository;

import static scheper.mateus.utils.ValidatorUtils.validarNulo;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioDTO findUsuarioDTOPorIdUsuario(Long idUsuario) {
        validarNulo("ID de usuário não pode ser nulo.", idUsuario);
        UsuarioDTO usuarioDTO = usuarioRepository.findUsuarioPorIdUsuario(idUsuario);
        validarNulo("Usuário não encontrado.", usuarioDTO);
        return usuarioDTO;
    }

    public Usuario findUsuarioPorIdUsuario(Long idUsuario) {
        return usuarioRepository.getById(idUsuario);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(username);
        if (usuario == null)
            throw new UsernameNotFoundException("Usuário não encontrado.");

        return User
                .builder()
                .username(usuario.getEmail())
                .password(usuario.getSenha())
                .roles("USER")
                .build();
    }
}
