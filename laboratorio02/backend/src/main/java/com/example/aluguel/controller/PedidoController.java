package com.example.aluguel.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.aluguel.dto.AvaliarRequest;
import com.example.aluguel.dto.ContratoRequest;
import com.example.aluguel.dto.PedidoRequest;
import com.example.aluguel.dto.PedidoResposta;
import com.example.aluguel.service.PedidoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public List<PedidoResposta> listar() {
        return pedidoService.listar();
    }

    @GetMapping("/{id}")
    public PedidoResposta buscar(@PathVariable Long id) {
        return pedidoService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResposta criar(@Valid @RequestBody PedidoRequest pedido) {
        return pedidoService.criar(pedido);
    }

    @PutMapping("/{id}")
    public PedidoResposta atualizar(@PathVariable Long id, @Valid @RequestBody PedidoRequest pedido) {
        return pedidoService.atualizar(id, pedido);
    }

    @PostMapping("/{id}/cancelar")
    public PedidoResposta cancelar(@PathVariable Long id) {
        return pedidoService.cancelar(id);
    }

    @PostMapping("/{id}/analise")
    public PedidoResposta assumir(@PathVariable Long id) {
        return pedidoService.assumir(id);
    }

    @PostMapping("/{id}/avaliar")
    public PedidoResposta avaliar(@PathVariable Long id, @Valid @RequestBody AvaliarRequest pedido) {
        return pedidoService.avaliar(id, pedido);
    }

    @PostMapping("/{id}/executar")
    public PedidoResposta executar(@PathVariable Long id) {
        return pedidoService.executar(id);
    }

    @PostMapping("/{id}/contrato")
    public PedidoResposta contratar(@PathVariable Long id, @Valid @RequestBody ContratoRequest pedido) {
        return pedidoService.contratar(id, pedido);
    }
}
