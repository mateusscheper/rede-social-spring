package scheper.mateus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import scheper.mateus.dto.UsuarioDTO;
import scheper.mateus.service.UsuarioService;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/{idUsuario}")
    @ResponseStatus(HttpStatus.OK)
    public UsuarioDTO findUsuarioPorIdUsuario(@PathVariable("idUsuario") Long idUsuario) {
        return usuarioService.findUsuarioDTOPorIdUsuario(idUsuario);
    }
}