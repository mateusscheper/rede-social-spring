package scheper.mateus.service;

import org.springframework.stereotype.Service;
import scheper.mateus.dto.NovoUsuarioDTO;
import scheper.mateus.dto.UsuarioDTO;
import scheper.mateus.entity.Usuario;
import scheper.mateus.exception.BusinessException;
import scheper.mateus.repository.UsuarioRepository;

import java.util.List;

import static scheper.mateus.utils.ValidatorUtils.validarNulo;

@Service
public class UsuarioService {

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

    public NovoUsuarioDTO save(NovoUsuarioDTO novoUsuarioDTO) {
        if (usuarioRepository.existsPorEmail(novoUsuarioDTO.getEmail()))
            throw new BusinessException("{usuario.validacao.emailJaCadastrado}");

        Usuario usuario = new Usuario(novoUsuarioDTO);
        usuarioRepository.save(usuario);

        novoUsuarioDTO.setIdUsuario(usuario.getIdUsuario());
        novoUsuarioDTO.setEmail(usuario.getEmail());
        return novoUsuarioDTO;
    }
}
