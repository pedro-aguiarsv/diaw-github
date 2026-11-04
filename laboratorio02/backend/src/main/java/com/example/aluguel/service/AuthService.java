package com.example.aluguel.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.aluguel.dto.LoginRequest;
import com.example.aluguel.dto.RegistrarRequest;
import com.example.aluguel.dto.UsuarioResposta;
import com.example.aluguel.model.Agente;
import com.example.aluguel.model.Papel;
import com.example.aluguel.model.Usuario;
import com.example.aluguel.repository.AgenteRepository;
import com.example.aluguel.repository.UsuarioRepository;
import com.example.aluguel.security.Sessao;
import com.example.aluguel.security.SessaoAtual;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

@Service
public class AuthService {

    private final UsuarioRepository usuarios;
    private final AgenteRepository agentes;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final SessaoAtual sessaoAtual;

    public AuthService(
            UsuarioRepository usuarios,
            AgenteRepository agentes,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            SecurityContextRepository securityContextRepository,
            SessaoAtual sessaoAtual) {
        this.usuarios = usuarios;
        this.agentes = agentes;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
        this.sessaoAtual = sessaoAtual;
    }

    @Transactional
    public UsuarioResposta registrar(RegistrarRequest pedido) {
        String login = pedido.usuario().trim().toLowerCase();
        if (usuarios.existsByUsuario(login)) {
            throw new RegraNegocioException(HttpStatus.CONFLICT, "Esse usuário já existe.");
        }
        if (pedido.papel() == Papel.AGENTE && pedido.tipoAgente() == null) {
            throw new RegraNegocioException(HttpStatus.BAD_REQUEST, "Informe se o agente é empresa ou banco.");
        }
        if (pedido.papel() == Papel.AGENTE && (pedido.razaoSocial() == null || pedido.razaoSocial().isBlank())) {
            throw new RegraNegocioException(HttpStatus.BAD_REQUEST, "Informe a razão social do agente.");
        }
        if (pedido.papel() == Papel.CLIENTE && pedido.tipoAgente() != null) {
            throw new RegraNegocioException(HttpStatus.BAD_REQUEST, "Cliente não possui tipo de agente.");
        }

        Usuario usuario = new Usuario();
        usuario.setUsuario(login);
        usuario.setSenha(passwordEncoder.encode(pedido.senha()));
        usuario.setNome(pedido.nome().trim());
        usuario.setPapel(pedido.papel());
        usuario.setTipoAgente(pedido.papel() == Papel.AGENTE ? pedido.tipoAgente() : null);
        usuarios.save(usuario);

        if (pedido.papel() == Papel.AGENTE) {
            Agente agente = new Agente();
            agente.setUsuario(usuario);
            agente.setTipo(pedido.tipoAgente());
            agente.setNome(pedido.nome().trim());
            agente.setRazaoSocial(pedido.razaoSocial().trim());
            agentes.save(agente);
        }
        return new UsuarioResposta(usuario.getId(), usuario.getUsuario(), usuario.getNome(), usuario.getPapel(), usuario.getTipoAgente());
    }

    public UsuarioResposta login(LoginRequest pedido, HttpServletRequest request, HttpServletResponse response) {
        Authentication autenticacao = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(pedido.usuario().trim().toLowerCase(), pedido.senha()));
        SecurityContext contexto = SecurityContextHolder.createEmptyContext();
        contexto.setAuthentication(autenticacao);
        SecurityContextHolder.setContext(contexto);
        securityContextRepository.saveContext(contexto, request, response);
        return UsuarioResposta.de((Sessao) autenticacao.getPrincipal());
    }

    public UsuarioResposta me() {
        return UsuarioResposta.de(sessaoAtual.exigir());
    }

    public void sair(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
    }
}
