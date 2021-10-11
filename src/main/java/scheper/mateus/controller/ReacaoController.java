package scheper.mateus.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import scheper.mateus.dto.ReacaoDTO;
import scheper.mateus.dto.ReagirDTO;
import scheper.mateus.service.ReacaoService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/reacao")
public class ReacaoController {

    private final ReacaoService reacaoService;

    public ReacaoController(ReacaoService reacaoService) {
        this.reacaoService = reacaoService;
    }

    @PostMapping
    public void reagir(@RequestBody @Valid ReagirDTO reagirDTO) {
        reacaoService.reagir(reagirDTO);
    }

    @GetMapping()
    public List<ReacaoDTO> findReacoesPorIdPostOuIdComentario(@RequestParam(value = "idPost", required = false) Long idPost,
                                                              @RequestParam(value = "idComentario", required = false) Long idComentario,
                                                              @RequestParam("idUsuario") Long idUsuario) {
        return reacaoService.findReacoesPorIdPostOuIdComentario(idPost, idComentario, idUsuario);
    }
}