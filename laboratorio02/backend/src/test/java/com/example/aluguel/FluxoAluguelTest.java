package com.example.aluguel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.aluguel.dto.AutomovelRequest;
import com.example.aluguel.dto.AvaliarRequest;
import com.example.aluguel.dto.ClienteRequest;
import com.example.aluguel.dto.ContratoRequest;
import com.example.aluguel.dto.PedidoRequest;
import com.example.aluguel.dto.RegistrarRequest;
import com.example.aluguel.model.Papel;
import com.example.aluguel.model.StatusPedido;
import com.example.aluguel.model.TipoAgente;
import com.example.aluguel.security.Sessao;
import com.example.aluguel.security.UsuarioDetailsService;
import com.example.aluguel.service.AuthService;
import com.example.aluguel.service.AutomovelService;
import com.example.aluguel.service.ClienteService;
import com.example.aluguel.service.PedidoService;

@SpringBootTest
class FluxoAluguelTest {

    @Autowired AuthService authService;
    @Autowired ClienteService clienteService;
    @Autowired AutomovelService automovelService;
    @Autowired PedidoService pedidoService;
    @Autowired UsuarioDetailsService usuarios;

    @AfterEach
    void limpa() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void clienteAbrePedidoEBancoConcedeCredito() {
        authService.registrar(new RegistrarRequest("lia", "senha123", "Lia Costa", Papel.CLIENTE, null, null));
        authService.registrar(new RegistrarRequest("serra", "senha123", "Banco Serra Teste", Papel.AGENTE, TipoAgente.BANCO, "Banco Serra Teste"));
        entrar("lia");

        clienteService.criar(new ClienteRequest(
                "MG-998877",
                "390.533.447-05",
                "Lia Costa",
                "Avenida Afonso Pena, 400",
                "Designer",
                java.util.List.of(new ClienteRequest.EmpregadorItem("Oficina Lume", new BigDecimal("4200.00")))));

        var carro = automovelService.criar(new AutomovelRequest("LAB-001", 2024, "Fiat", "Fastback", "LAB1A23"));
        var pedido = pedidoService.criar(new PedidoRequest(
                carro.id(),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(4),
                new BigDecimal("150.00"),
                "Fim de semana"));
        assertEquals(StatusPedido.INTRODUZIDO, pedido.status());
        assertEquals(new BigDecimal("450.00"), pedido.valorTotal());

        entrar("serra");
        var avaliado = pedidoService.avaliar(pedido.id(), new AvaliarRequest("POSITIVO", "Renda compatível com a diária."));
        assertEquals(StatusPedido.PARECER_POSITIVO, avaliado.status());

        var contratado = pedidoService.contratar(pedido.id(), new ContratoRequest(new BigDecimal("450.00"), 3, new BigDecimal("1.90")));
        assertEquals(StatusPedido.CONTRATO_EM_EXECUCAO, contratado.status());
        assertEquals("Banco Serra Teste", contratado.contrato().bancoNome());
    }

    private void entrar(String login) {
        Sessao sessao = (Sessao) usuarios.loadUserByUsername(login);
        var autenticacao = new UsernamePasswordAuthenticationToken(sessao, null, sessao.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(autenticacao);
    }
}
