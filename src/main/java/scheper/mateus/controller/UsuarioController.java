package scheper.mateus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import scheper.mateus.dto.NovoUsuarioDTO;
import scheper.mateus.dto.UsuarioDTO;
import scheper.mateus.service.UsuarioService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NovoUsuarioDTO save(@RequestBody @Valid NovoUsuarioDTO novoUsuarioDTO) {
        return usuarioService.save(novoUsuarioDTO);
    }

    @GetMapping("/{idUsuario}")
    @ResponseStatus(HttpStatus.OK)
    public UsuarioDTO findUsuarioPorIdUsuario(@PathVariable("idUsuario") Long idUsuario) {
        return usuarioService.findUsuarioDTOPorIdUsuario(idUsuario);
    }
}