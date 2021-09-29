package scheper.mateus.service;

import org.springframework.stereotype.Service;
import scheper.mateus.dto.NovoUsuarioDTO;
import scheper.mateus.dto.UsuarioDTO;
import scheper.mateus.entity.Usuario;
import scheper.mateus.exception.UsuarioBusinessException;
import scheper.mateus.repository.UsuarioRepository;

import java.util.List;

import static scheper.mateus.utils.ValidatorUtils.validarNulo;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<UsuarioDTO> findAmigosByIdUsuario(Long idUsuario) {
        validarNulo(idUsuario, "ID de usuário não pode ser nulo.");
        return usuarioRepository.findAmigosByIdUsuario(idUsuario);
    }

    public UsuarioDTO findUsuarioByIdUsuario(Long idUsuario) {
        validarNulo(idUsuario, "ID de usuário não pode ser nulo.");
        UsuarioDTO usuarioDTO = usuarioRepository.findUsuarioByIdUsuario(idUsuario);
        validarNulo(usuarioDTO, "Usuário não encontrado.");

        return usuarioDTO;
    }

    public NovoUsuarioDTO save(NovoUsuarioDTO novoUsuarioDTO) {
        if (usuarioRepository.existsByEmail(novoUsuarioDTO.getEmail()))
            throw new UsuarioBusinessException("{usuario.validacao.emailJaCadastrado}");

        Usuario usuario = new Usuario(novoUsuarioDTO);
        usuarioRepository.save(usuario);

        novoUsuarioDTO.setIdUsuario(usuario.getIdUsuario());
        novoUsuarioDTO.setEmail(usuario.getEmail());
        return novoUsuarioDTO;
    }

    public Usuario findUsuarioById(Long idUsuario) {
        return usuarioRepository.getById(idUsuario);
    }
}
