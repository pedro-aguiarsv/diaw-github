package com.example.aluguel.dto;

import com.example.aluguel.model.Automovel;
import com.example.aluguel.model.TipoProprietario;

public record AutomovelResposta(
        Long id,
        String matricula,
        Integer ano,
        String marca,
        String modelo,
        String placa,
        TipoProprietario tipoProprietario,
        String proprietarioNome,
        boolean proprio
) {
    public static AutomovelResposta de(Automovel automovel, Long usuarioId) {
        String dono;
        boolean proprio;
        if (automovel.getProprietarioCliente() != null) {
            dono = automovel.getProprietarioCliente().getNome();
            proprio = automovel.getProprietarioCliente().getUsuario().getId().equals(usuarioId);
        } else if (automovel.getProprietarioAgente() != null) {
            dono = automovel.getProprietarioAgente().getRazaoSocial();
            proprio = automovel.getProprietarioAgente().getUsuario().getId().equals(usuarioId);
        } else {
            dono = "Não informado";
            proprio = false;
        }
        return new AutomovelResposta(
                automovel.getId(),
                automovel.getMatricula(),
                automovel.getAno(),
                automovel.getMarca(),
                automovel.getModelo(),
                automovel.getPlaca(),
                automovel.getTipoProprietario(),
                dono,
                proprio);
    }
}
