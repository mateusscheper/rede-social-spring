package scheper.mateus.service;

import org.springframework.stereotype.Service;
import scheper.mateus.dto.NotificacaoDTO;
import scheper.mateus.entity.Notificacao;
import scheper.mateus.entity.Usuario;
import scheper.mateus.repository.NotificacaoRepository;
import scheper.mateus.utils.ValidatorUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;

    private final JwtService jwtService;

    public NotificacaoService(NotificacaoRepository notificacaoRepository, JwtService jwtService) {
        this.notificacaoRepository = notificacaoRepository;
        this.jwtService = jwtService;
    }

    public void criarNotificacao(Usuario usuario, Usuario usuarioRecebedor, String conteudo, String url) {
        ValidatorUtils.validarNulo("{notificacao.validacao.vazio}", usuario, conteudo, url);
        Notificacao notificacao = new Notificacao(usuario, usuarioRecebedor, conteudo, url);
        notificacaoRepository.save(notificacao);
    }

    public void removerNotificacaoAdicionar(Long idUsuarioLogado, Long idUsuarioAmigo) {
        List<Notificacao> notificacoes = notificacaoRepository.obterNotificacaoPorUsuarios(idUsuarioLogado, idUsuarioAmigo);
        notificacaoRepository.deleteAll(notificacoes);
    }

    public List<NotificacaoDTO> consultarNotificacoesRecentes(HttpServletRequest request) {
        Usuario usuario = jwtService.obterUsuarioDoRequest(request);
        ValidatorUtils.validarNulo("{usuario.validacao.naoEncontrado}", usuario);

        List<Usuario> amigosDoUsuario = obterAmigosPorUsuario(usuario);
        List<Long> idsUsuarioCriadores = mapearIdsUsuarios(amigosDoUsuario);

        if (idsUsuarioCriadores.isEmpty())
            return new ArrayList<>();

        List<Notificacao> notificacoes = notificacaoRepository.consultarNotificacoesRecentes(idsUsuarioCriadores, usuario.getIdUsuario());

        return notificacoes
                .stream()
                .map(NotificacaoDTO::new)
                .toList();
    }

    private List<Usuario> obterAmigosPorUsuario(Usuario usuario) {
        return usuario.getAmigos()
                .stream()
                .filter(amigo -> isAdicionado(amigo, usuario.getIdUsuario()))
                .toList();
    }

    private boolean isAdicionado(Usuario amigo, Long idUsuario) {
        return amigo.getAmigos()
                .stream()
                .anyMatch(u -> u.getIdUsuario().equals(idUsuario));
    }

    private List<Long> mapearIdsUsuarios(List<Usuario> amigosDoUsuario) {
        return amigosDoUsuario
                .stream()
                .map(Usuario::getIdUsuario)
                .toList();
    }
}