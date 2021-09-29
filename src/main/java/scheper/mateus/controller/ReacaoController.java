package scheper.mateus.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @GetMapping("/post/{idPost}/usuario/{idUsuario}")
    public List<ReacaoDTO> obterReacoesByIdPostAndIdUsuario(@PathVariable("idPost") Long idPost, @PathVariable("idUsuario") Long idUsuario) {
        return reacaoService.obterReacoesByIdPost(idPost, idUsuario);
    }
}