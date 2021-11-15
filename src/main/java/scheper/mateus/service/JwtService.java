package scheper.mateus.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import scheper.mateus.dto.LoginDTO;
import scheper.mateus.dto.UsuarioSimplesDTO;
import scheper.mateus.entity.Usuario;
import scheper.mateus.exception.BusinessException;
import scheper.mateus.repository.UsuarioRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value(value = "${jwt.security.expiracao}")
    private String expiracao;

    @Value(value = "${jwt.security.chave}")
    private String chaveAssinatura;

    private final UsuarioRepository usuarioRepository;

    public JwtService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public LoginDTO gerarToken(Usuario usuario) {
        long tempoExpiracao = Long.parseLong(expiracao);
        LocalDateTime dataHoraExpiracao = LocalDateTime.now().plusMinutes(tempoExpiracao);

        Map<String, Object> informacoesAdicionais = new HashMap<>();
        informacoesAdicionais.put(Claims.SUBJECT, usuario.getEmail());
        informacoesAdicionais.put("idUsuario", usuario.getIdUsuario());

        String token = Jwts
                .builder()
                .setClaims(informacoesAdicionais)
                .setExpiration(Date.from(dataHoraExpiracao.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS512, chaveAssinatura)
                .compact();

        return new LoginDTO(new UsuarioSimplesDTO(usuario, false), token);
    }

    public Claims obterClaims(String token) {
        return Jwts
                .parser()
                .setSigningKey(chaveAssinatura)
                .parseClaimsJws(token)
                .getBody();
    }

    public String obterEmailByToken(String token) {
        return Jwts
                .parser()
                .setSigningKey(chaveAssinatura)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Usuario obterUsuarioDoRequest(HttpServletRequest request) {
        String email = obterEmailDoRequest(request);
        return usuarioRepository.findByEmail(email);
    }

    public String obterEmailDoRequest(HttpServletRequest request) {
        String token = obterTokenDoRequest(request);
        String email = obterEmailByToken(token);
        if (ObjectUtils.isEmpty(email))
            throw new BusinessException("usuario.validacao.naoEncontrado");
        return email;
    }

    private String obterTokenDoRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (!token.startsWith("Bearer "))
            throw new BusinessException("usuario.validacao.naoEncontrado");
        return token.replace("Bearer ", "");
    }

    public boolean isTokenValido(String token) {
        if (ObjectUtils.isEmpty(token))
            return false;

        try {
            Claims claims = obterClaims(token);
            Date dataExpiracao = claims.getExpiration();
            LocalDateTime data = dataExpiracao.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            return !LocalDateTime.now().isAfter(data);
        } catch (Exception e) {
            return false;
        }
    }
}