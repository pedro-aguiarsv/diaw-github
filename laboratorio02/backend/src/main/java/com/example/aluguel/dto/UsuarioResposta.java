package com.example.aluguel.dto;

import com.example.aluguel.model.Papel;
import com.example.aluguel.model.TipoAgente;
import com.example.aluguel.security.Sessao;

public record UsuarioResposta(Long id, String usuario, String nome, Papel papel, TipoAgente tipoAgente) {

    public static UsuarioResposta de(Sessao sessao) {
        return new UsuarioResposta(sessao.getId(), sessao.getUsername(), sessao.getNome(), sessao.getPapel(), sessao.getTipoAgente());
    }
}
