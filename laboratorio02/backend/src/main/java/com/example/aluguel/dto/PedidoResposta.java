package com.example.aluguel.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.example.aluguel.model.Pedido;
import com.example.aluguel.model.StatusPedido;

public record PedidoResposta(
        Long id,
        StatusPedido status,
        LocalDate dataInicio,
        LocalDate dataFim,
        BigDecimal valorDiaria,
        BigDecimal valorTotal,
        String observacao,
        String parecer,
        String agenteNome,
        ClienteResposta cliente,
        AutomovelResumo automovel,
        ContratoResumo contrato
) {
    public record AutomovelResumo(Long id, String marca, String modelo, String placa, Integer ano, String matricula) {
    }

    public record ContratoResumo(BigDecimal valorFinanciado, Integer numeroParcelas, BigDecimal taxaMensal, String bancoNome) {
    }

    public static PedidoResposta de(Pedido pedido) {
        ContratoResumo contrato = null;
        if (pedido.getContrato() != null) {
            contrato = new ContratoResumo(
                    pedido.getContrato().getValorFinanciado(),
                    pedido.getContrato().getNumeroParcelas(),
                    pedido.getContrato().getTaxaMensal(),
                    pedido.getContrato().getBanco().getRazaoSocial());
        }
        String agente = pedido.getAgenteAvaliador() == null ? null : pedido.getAgenteAvaliador().getRazaoSocial();
        return new PedidoResposta(
                pedido.getId(),
                pedido.getStatus(),
                pedido.getDataInicio(),
                pedido.getDataFim(),
                pedido.getValorDiaria(),
                pedido.getValorTotal(),
                pedido.getObservacao(),
                pedido.getParecer(),
                agente,
                ClienteResposta.de(pedido.getCliente()),
                new AutomovelResumo(
                        pedido.getAutomovel().getId(),
                        pedido.getAutomovel().getMarca(),
                        pedido.getAutomovel().getModelo(),
                        pedido.getAutomovel().getPlaca(),
                        pedido.getAutomovel().getAno(),
                        pedido.getAutomovel().getMatricula()),
                contrato);
    }

    public static List<PedidoResposta> de(List<Pedido> pedidos) {
        return pedidos.stream().map(PedidoResposta::de).toList();
    }
}
