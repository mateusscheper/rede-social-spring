package scheper.mateus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import scheper.mateus.dto.NovoUsuarioDTO;
import scheper.mateus.dto.PostDTO;
import scheper.mateus.dto.UsuarioDTO;
import scheper.mateus.service.PostService;
import scheper.mateus.service.UsuarioService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final PostService postService;

    public UsuarioController(UsuarioService usuarioService, PostService postService) {
        this.usuarioService = usuarioService;
        this.postService = postService;
    }

    @GetMapping("/{idUsuario}")
    @ResponseStatus(HttpStatus.OK)
    public UsuarioDTO findUsuarioByIdUsuario(@PathVariable("idUsuario") Long idUsuario) {
        return usuarioService.findUsuarioByIdUsuario(idUsuario);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NovoUsuarioDTO save(@RequestBody @Valid NovoUsuarioDTO novoUsuarioDTO) {
        return usuarioService.save(novoUsuarioDTO);
    }

    @GetMapping("/{idUsuario}/amigo")
    @ResponseStatus(HttpStatus.OK)
    public List<UsuarioDTO> findAmigosByIdUsuario(@PathVariable("idUsuario") Long idUsuario) {
        return usuarioService.findAmigosByIdUsuario(idUsuario);
    }

    @GetMapping("/{idUsuario}/post")
    @ResponseStatus(HttpStatus.OK)
    public List<PostDTO> findPostsByIdUsuario(@PathVariable("idUsuario") Long idUsuario) {
        return postService.findPostsByIdUsuario(idUsuario);
    }
}