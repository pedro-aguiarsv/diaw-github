package com.example.aluguel.dto;

import java.math.BigDecimal;
import java.util.List;

import com.example.aluguel.model.Cliente;

public record ClienteResposta(
        Long id,
        Long usuarioId,
        String rg,
        String cpf,
        String nome,
        String endereco,
        String profissao,
        List<EmpregadorResumo> empregadores
) {
    public record EmpregadorResumo(String entidade, BigDecimal rendimento) {
    }

    public static ClienteResposta de(Cliente cliente) {
        List<EmpregadorResumo> rendas = cliente.getEmpregadores().stream()
                .map(item -> new EmpregadorResumo(item.getEntidade(), item.getRendimento()))
                .toList();
        return new ClienteResposta(
                cliente.getId(),
                cliente.getUsuario().getId(),
                cliente.getRg(),
                cliente.getCpf(),
                cliente.getNome(),
                cliente.getEndereco(),
                cliente.getProfissao(),
                rendas);
    }
}
