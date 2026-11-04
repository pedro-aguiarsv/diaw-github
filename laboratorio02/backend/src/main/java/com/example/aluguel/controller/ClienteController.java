package com.example.aluguel.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.aluguel.dto.ClienteRequest;
import com.example.aluguel.dto.ClienteResposta;
import com.example.aluguel.service.ClienteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public List<ClienteResposta> listar() {
        return clienteService.listar();
    }

    @GetMapping("/me")
    public ClienteResposta me() {
        return clienteService.me();
    }

    @GetMapping("/{id}")
    public ClienteResposta buscar(@PathVariable Long id) {
        return clienteService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResposta criar(@Valid @RequestBody ClienteRequest pedido) {
        return clienteService.criar(pedido);
    }

    @PutMapping("/{id}")
    public ClienteResposta atualizar(@PathVariable Long id, @Valid @RequestBody ClienteRequest pedido) {
        return clienteService.atualizar(id, pedido);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        clienteService.remover(id);
    }
}
