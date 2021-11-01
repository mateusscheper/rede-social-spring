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

        return new LoginDTO(new UsuarioSimplesDTO(usuario), token);
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