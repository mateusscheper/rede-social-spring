package scheper.mateus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import scheper.mateus.dto.LoginRegistroDTO;
import scheper.mateus.dto.NovoUsuarioDTO;
import scheper.mateus.dto.LoginDTO;
import scheper.mateus.service.AuthService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginDTO login(@RequestBody @Valid LoginRegistroDTO loginDTO) {
        return authService.login(loginDTO);
    }

    @PostMapping("/valido")
    public void validarToken(@RequestBody String token) {
        authService.validarToken(token);
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public void registrar(@RequestBody @Valid NovoUsuarioDTO novoUsuarioDTO) {
        authService.registrar(novoUsuarioDTO);
    }
}