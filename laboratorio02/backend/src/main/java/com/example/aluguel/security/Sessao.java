package com.example.aluguel.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.aluguel.model.Papel;
import com.example.aluguel.model.TipoAgente;
import com.example.aluguel.model.Usuario;

public class Sessao implements UserDetails {

    private final Long id;
    private final String usuario;
    private final String senha;
    private final String nome;
    private final Papel papel;
    private final TipoAgente tipoAgente;

    public Sessao(Usuario usuario) {
        this.id = usuario.getId();
        this.usuario = usuario.getUsuario();
        this.senha = usuario.getSenha();
        this.nome = usuario.getNome();
        this.papel = usuario.getPapel();
        this.tipoAgente = usuario.getTipoAgente();
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public Papel getPapel() { return papel; }
    public TipoAgente getTipoAgente() { return tipoAgente; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> autoridades = new ArrayList<>();
        autoridades.add(new SimpleGrantedAuthority("ROLE_" + papel.name()));
        if (tipoAgente != null) {
            autoridades.add(new SimpleGrantedAuthority("ROLE_" + tipoAgente.name()));
        }
        return autoridades;
    }

    @Override
    public String getPassword() { return senha; }

    @Override
    public String getUsername() { return usuario; }
}
