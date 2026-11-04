package com.example.aluguel.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.aluguel.dto.LoginRequest;
import com.example.aluguel.dto.RegistrarRequest;
import com.example.aluguel.dto.UsuarioResposta;
import com.example.aluguel.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registrar")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResposta registrar(@Valid @RequestBody RegistrarRequest pedido) {
        return authService.registrar(pedido);
    }

    @PostMapping("/login")
    public UsuarioResposta login(@Valid @RequestBody LoginRequest pedido, HttpServletRequest request, HttpServletResponse response) {
        return authService.login(pedido, request, response);
    }

    @GetMapping("/me")
    public UsuarioResposta me() {
        return authService.me();
    }

    @PostMapping("/sair")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void sair(HttpServletRequest request, HttpServletResponse response) {
        authService.sair(request, response);
    }
}
