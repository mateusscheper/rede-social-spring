package scheper.mateus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import scheper.mateus.dto.ComentarioDTO;
import scheper.mateus.dto.NovoComentarioDTO;
import scheper.mateus.service.ComentarioService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
    public void save(@RequestBody @Valid NovoComentarioDTO novoComentarioDTO) {
        comentarioService.save(novoComentarioDTO);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ComentarioDTO> findComentariosPorIdPost(@RequestParam("idPost") Long idPost,
                                                        @RequestParam("idUsuario") Long idUsuario,
                                                        @RequestParam("limit") Integer limit) {
        return comentarioService.findComentariosPorIdPost(idPost, idUsuario, limit);
    }

    @GetMapping("/{idComentario}/subcomentario")
    @ResponseStatus(HttpStatus.OK)
    public List<ComentarioDTO> findSubcomentariosPorIdComentario(@PathVariable("idComentario") @NotNull Long idComentarioPai,
                                                                 @RequestParam("idUsuario") @NotNull Long idUsuario) {
        return comentarioService.findSubcomentariosPorIdComentario(idComentarioPai, idUsuario);
    }
}