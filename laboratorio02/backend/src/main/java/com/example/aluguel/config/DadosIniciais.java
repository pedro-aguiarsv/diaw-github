package com.example.aluguel.config;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.aluguel.model.Agente;
import com.example.aluguel.model.Automovel;
import com.example.aluguel.model.Cliente;
import com.example.aluguel.model.Empregador;
import com.example.aluguel.model.Papel;
import com.example.aluguel.model.Pedido;
import com.example.aluguel.model.StatusPedido;
import com.example.aluguel.model.TipoAgente;
import com.example.aluguel.model.TipoProprietario;
import com.example.aluguel.model.Usuario;
import com.example.aluguel.repository.AgenteRepository;
import com.example.aluguel.repository.AutomovelRepository;
import com.example.aluguel.repository.ClienteRepository;
import com.example.aluguel.repository.PedidoRepository;
import com.example.aluguel.repository.UsuarioRepository;

@Component
@Profile("!test")
public class DadosIniciais implements ApplicationRunner {

    private final UsuarioRepository usuarios;
    private final AgenteRepository agentes;
    private final ClienteRepository clientes;
    private final AutomovelRepository automoveis;
    private final PedidoRepository pedidos;
    private final PasswordEncoder passwordEncoder;

    public DadosIniciais(
            UsuarioRepository usuarios,
            AgenteRepository agentes,
            ClienteRepository clientes,
            AutomovelRepository automoveis,
            PedidoRepository pedidos,
            PasswordEncoder passwordEncoder) {
        this.usuarios = usuarios;
        this.agentes = agentes;
        this.clientes = clientes;
        this.automoveis = automoveis;
        this.pedidos = pedidos;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (usuarios.count() > 0) {
            return;
        }
        Agente banco = agente("banco", "Helena Prado", "Banco Serra", TipoAgente.BANCO);
        Agente empresa = agente("empresa", "Caio Mendes", "Garagem Norte", TipoAgente.EMPRESA);
        Cliente ana = cliente();
        Automovel compass = carro("NORTE-014", 2023, "Jeep", "Compass", "RIO2A28", TipoProprietario.EMPRESA, null, empresa);
        carro("NORTE-021", 2024, "Toyota", "Corolla", "BRA1B19", TipoProprietario.EMPRESA, null, empresa);
        carro("NORTE-008", 2022, "Chevrolet", "Onix", "MIN3C07", TipoProprietario.BANCO, null, banco);
        carro("NORTE-033", 2021, "Chevrolet", "Tracker", "ANA4D10", TipoProprietario.CLIENTE, ana, null);

        Pedido pedido = new Pedido();
        pedido.setCliente(ana);
        pedido.setAutomovel(compass);
        pedido.setDataInicio(LocalDate.now().plusDays(5));
        pedido.setDataFim(LocalDate.now().plusDays(12));
        pedido.setValorDiaria(new BigDecimal("280.00"));
        pedido.setValorTotal(new BigDecimal("1960.00"));
        pedido.setStatus(StatusPedido.INTRODUZIDO);
        pedido.setObservacao("Retirada na Savassi.");
        pedidos.save(pedido);
    }

    private Agente agente(String login, String nome, String razao, TipoAgente tipo) {
        Usuario usuario = usuario(login, nome, Papel.AGENTE, tipo);
        Agente agente = new Agente();
        agente.setUsuario(usuario);
        agente.setNome(nome);
        agente.setRazaoSocial(razao);
        agente.setTipo(tipo);
        return agentes.save(agente);
    }

    private Cliente cliente() {
        Usuario usuario = usuario("ana", "Ana Lima", Papel.CLIENTE, null);
        Cliente cliente = new Cliente();
        cliente.setUsuario(usuario);
        cliente.setNome("Ana Lima");
        cliente.setRg("MG-12.345.678");
        cliente.setCpf("52998224725");
        cliente.setEndereco("Rua da Bahia, 1200, Lourdes, Belo Horizonte");
        cliente.setProfissao("Analista de sistemas");
        Empregador emprego = new Empregador();
        emprego.setCliente(cliente);
        emprego.setEntidade("Estúdio Norte");
        emprego.setRendimento(new BigDecimal("6800.00"));
        cliente.getEmpregadores().add(emprego);
        return clientes.save(cliente);
    }

    private Usuario usuario(String login, String nome, Papel papel, TipoAgente tipo) {
        Usuario usuario = new Usuario();
        usuario.setUsuario(login);
        usuario.setSenha(passwordEncoder.encode(login + "123"));
        usuario.setNome(nome);
        usuario.setPapel(papel);
        usuario.setTipoAgente(tipo);
        return usuarios.save(usuario);
    }

    private Automovel carro(
            String matricula,
            int ano,
            String marca,
            String modelo,
            String placa,
            TipoProprietario tipo,
            Cliente cliente,
            Agente agente) {
        Automovel automovel = new Automovel();
        automovel.setMatricula(matricula);
        automovel.setAno(ano);
        automovel.setMarca(marca);
        automovel.setModelo(modelo);
        automovel.setPlaca(placa);
        automovel.setTipoProprietario(tipo);
        automovel.setProprietarioCliente(cliente);
        automovel.setProprietarioAgente(agente);
        return automoveis.save(automovel);
    }
}
