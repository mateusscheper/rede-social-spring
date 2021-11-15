package scheper.mateus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import scheper.mateus.dto.NotificacaoDTO;
import scheper.mateus.service.NotificacaoService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/notificacao")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    public NotificacaoController(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    @GetMapping("/recente")
    @ResponseStatus(HttpStatus.OK)
    public List<NotificacaoDTO> consultarNotificacoesRecentes(HttpServletRequest request) {
        return notificacaoService.consultarNotificacoesRecentes(request);
    }
}
