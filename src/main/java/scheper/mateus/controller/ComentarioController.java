package scheper.mateus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import scheper.mateus.dto.ComentarioDTO;
import scheper.mateus.dto.NovoComentarioDTO;
import scheper.mateus.service.ComentarioService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/comentario")
public class ComentarioController {

    private final ComentarioService comentarioService;

    public ComentarioController(ComentarioService comentarioService) {
        this.comentarioService = comentarioService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void saveComentario(@RequestBody @Valid NovoComentarioDTO novoComentarioDTO) {
        comentarioService.saveComentario(novoComentarioDTO);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ComentarioDTO> findComentariosByIdPost(@RequestParam("idPost") Long idPost,
                                                       @RequestParam("idUsuario") Long idUsuario,
                                                       @RequestParam("limit") Integer limit) {
        return comentarioService.findComentariosByIdPost(idPost, idUsuario, limit);
    }
}