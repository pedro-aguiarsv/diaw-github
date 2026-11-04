package com.example.aluguel.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.aluguel.service.RegraNegocioException;

@Component
public class SessaoAtual {

    public Sessao exigir() {
        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacao == null || !(autenticacao.getPrincipal() instanceof Sessao sessao)) {
            throw new RegraNegocioException(HttpStatus.UNAUTHORIZED, "Faça login para continuar.");
        }
        return sessao;
    }
}
