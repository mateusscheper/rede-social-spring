package scheper.mateus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import scheper.mateus.dto.UsuarioCompletoDTO;
import scheper.mateus.service.UsuarioService;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public UsuarioCompletoDTO findUsuarioCompletoPorToken(HttpServletRequest request) {
        return usuarioService.findUsuarioCompletoPorToken(request);
    }

    @GetMapping("/{idUsuario}")
    @ResponseStatus(HttpStatus.OK)
    public UsuarioCompletoDTO findUsuarioPorIdUsuario(@PathVariable("idUsuario") Long idUsuario) {
        return usuarioService.findUsuarioPorIdUsuario(idUsuario);
    }
}