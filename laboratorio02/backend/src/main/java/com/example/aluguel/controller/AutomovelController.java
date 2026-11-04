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

import com.example.aluguel.dto.AutomovelRequest;
import com.example.aluguel.dto.AutomovelResposta;
import com.example.aluguel.service.AutomovelService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/automoveis")
public class AutomovelController {

    private final AutomovelService automovelService;

    public AutomovelController(AutomovelService automovelService) {
        this.automovelService = automovelService;
    }

    @GetMapping
    public List<AutomovelResposta> listar() {
        return automovelService.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AutomovelResposta criar(@Valid @RequestBody AutomovelRequest pedido) {
        return automovelService.criar(pedido);
    }

    @PutMapping("/{id}")
    public AutomovelResposta atualizar(@PathVariable Long id, @Valid @RequestBody AutomovelRequest pedido) {
        return automovelService.atualizar(id, pedido);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        automovelService.remover(id);
    }
}
