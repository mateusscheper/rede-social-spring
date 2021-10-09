package scheper.mateus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import scheper.mateus.dto.PostCompletoDTO;
import scheper.mateus.dto.PostDTO;
import scheper.mateus.dto.UsuarioDTO;
import scheper.mateus.service.PostService;
import scheper.mateus.service.UsuarioService;

import java.util.List;

@RestController
@RequestMapping("/api/post")
public class PostController {

    private final UsuarioService usuarioService;

    private final PostService postService;

    public PostController(UsuarioService usuarioService, PostService postService) {
        this.usuarioService = usuarioService;
        this.postService = postService;
    }

    @PostMapping(consumes = { "multipart/form-data", "application/json" })
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestPart String post, @RequestPart(value = "imagem", required = false) MultipartFile imagem) {
        postService.save(post, imagem);
    }

    @GetMapping("/{idUsuario}/amigo")
    @ResponseStatus(HttpStatus.OK)
    public List<UsuarioDTO> findAmigosByIdUsuario(@PathVariable("idUsuario") Long idUsuario) {
        return usuarioService.findAmigosByIdUsuario(idUsuario);
    }

    @GetMapping("/{idPost}")
    @ResponseStatus(HttpStatus.OK)
    public PostCompletoDTO findPostById(@PathVariable("idPost") Long idPost, @RequestParam("idUsuario") Long idUsuario) {
        return postService.findPostById(idPost, idUsuario);
    }
}