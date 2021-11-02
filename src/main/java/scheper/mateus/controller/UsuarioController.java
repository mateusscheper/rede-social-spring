package scheper.mateus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
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
    public UsuarioCompletoDTO findUsuarioPorIdUsuario(HttpServletRequest request, @PathVariable("idUsuario") Long idUsuario) {
        return usuarioService.findUsuarioPorIdUsuario(request, idUsuario);
    }

    @PostMapping("/{idUsuario}/adicionar")
    @ResponseStatus(HttpStatus.OK)
    public void adicionarAmigo(HttpServletRequest request, @PathVariable("idUsuario") Long idUsuario) {
        usuarioService.adicionarAmigo(request, idUsuario);
    }

    @PostMapping("/{idUsuario}/desfazer")
    @ResponseStatus(HttpStatus.OK)
    public void desfazerAmizade(HttpServletRequest request, @PathVariable("idUsuario") Long idUsuario) {
        usuarioService.desfazerAmizade(request, idUsuario);
    }

    @PostMapping("/{idUsuario}/aceitar")
    @ResponseStatus(HttpStatus.OK)
    public void aceitarAmizade(HttpServletRequest request, @PathVariable("idUsuario") Long idUsuario) {
        usuarioService.aceitarAmizade(request, idUsuario);
    }

    @PostMapping("/{idUsuario}/cancelar")
    @ResponseStatus(HttpStatus.OK)
    public void cancelarAdicionar(HttpServletRequest request, @PathVariable("idUsuario") Long idUsuario) {
        usuarioService.cancelarAdicionar(request, idUsuario);
    }
}