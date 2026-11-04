package com.example.aluguel.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.aluguel.dto.AvaliarRequest;
import com.example.aluguel.dto.ContratoRequest;
import com.example.aluguel.dto.PedidoRequest;
import com.example.aluguel.dto.PedidoResposta;
import com.example.aluguel.model.Agente;
import com.example.aluguel.model.Automovel;
import com.example.aluguel.model.Cliente;
import com.example.aluguel.model.ContratoCredito;
import com.example.aluguel.model.Papel;
import com.example.aluguel.model.Pedido;
import com.example.aluguel.model.StatusPedido;
import com.example.aluguel.model.TipoAgente;
import com.example.aluguel.repository.AgenteRepository;
import com.example.aluguel.repository.AutomovelRepository;
import com.example.aluguel.repository.ClienteRepository;
import com.example.aluguel.repository.PedidoRepository;
import com.example.aluguel.security.Sessao;
import com.example.aluguel.security.SessaoAtual;

@Service
@Transactional
public class PedidoService {

    private static final Set<StatusPedido> OCUPA_CARRO = EnumSet.of(
            StatusPedido.INTRODUZIDO,
            StatusPedido.EM_ANALISE,
            StatusPedido.PARECER_POSITIVO,
            StatusPedido.CONTRATO_EM_EXECUCAO);

    private final PedidoRepository pedidos;
    private final ClienteRepository clientes;
    private final AutomovelRepository automoveis;
    private final AgenteRepository agentes;
    private final SessaoAtual sessaoAtual;

    public PedidoService(
            PedidoRepository pedidos,
            ClienteRepository clientes,
            AutomovelRepository automoveis,
            AgenteRepository agentes,
            SessaoAtual sessaoAtual) {
        this.pedidos = pedidos;
        this.clientes = clientes;
        this.automoveis = automoveis;
        this.agentes = agentes;
        this.sessaoAtual = sessaoAtual;
    }

    @Transactional(readOnly = true)
    public List<PedidoResposta> listar() {
        Sessao sessao = sessaoAtual.exigir();
        List<Pedido> encontrados = sessao.getPapel() == Papel.AGENTE
                ? pedidos.listar()
                : pedidos.doCliente(clienteAtual().getId());
        return encontrados.stream()
                .collect(Collectors.toMap(Pedido::getId, Function.identity(), (a, b) -> a, LinkedHashMap::new))
                .values().stream()
                .map(PedidoResposta::de)
                .toList();
    }

    public PedidoResposta buscar(Long id) {
        return PedidoResposta.de(carregarVisivel(id));
    }

    public PedidoResposta criar(PedidoRequest pedido) {
        Cliente cliente = clienteAtual();
        Pedido novo = new Pedido();
        novo.setCliente(cliente);
        novo.setStatus(StatusPedido.INTRODUZIDO);
        aplicar(novo, pedido, null);
        return PedidoResposta.de(pedidos.save(novo));
    }

    public PedidoResposta atualizar(Long id, PedidoRequest pedido) {
        Sessao sessao = sessaoAtual.exigir();
        Pedido existente = carregarVisivel(id);
        if (sessao.getPapel() == Papel.CLIENTE) {
            if (existente.getStatus() != StatusPedido.INTRODUZIDO) {
                throw new RegraNegocioException(HttpStatus.CONFLICT, "O cliente só altera um pedido introduzido.");
            }
        } else if (existente.getStatus() != StatusPedido.INTRODUZIDO && existente.getStatus() != StatusPedido.EM_ANALISE) {
            throw new RegraNegocioException(HttpStatus.CONFLICT, "O agente só altera o pedido antes do parecer.");
        }
        aplicar(existente, pedido, existente.getId());
        return PedidoResposta.de(existente);
    }

    public PedidoResposta cancelar(Long id) {
        Cliente cliente = clienteAtual();
        Pedido pedido = carregarVisivel(id);
        if (!pedido.getCliente().getId().equals(cliente.getId())) {
            throw new RegraNegocioException(HttpStatus.FORBIDDEN, "Só o cliente cancela o próprio pedido.");
        }
        if (pedido.getStatus() == StatusPedido.CONTRATO_EM_EXECUCAO || pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new RegraNegocioException(HttpStatus.CONFLICT, "Este pedido não pode ser cancelado.");
        }
        pedido.setStatus(StatusPedido.CANCELADO);
        return PedidoResposta.de(pedido);
    }

    public PedidoResposta assumir(Long id) {
        exigirAgente();
        Pedido pedido = carregarVisivel(id);
        if (pedido.getStatus() != StatusPedido.INTRODUZIDO) {
            throw new RegraNegocioException(HttpStatus.CONFLICT, "Só um pedido introduzido entra em análise.");
        }
        pedido.setStatus(StatusPedido.EM_ANALISE);
        pedido.setAgenteAvaliador(agenteAtual());
        return PedidoResposta.de(pedido);
    }

    public PedidoResposta avaliar(Long id, AvaliarRequest pedidoAvaliacao) {
        exigirAgente();
        Pedido pedido = carregarVisivel(id);
        if (pedido.getStatus() != StatusPedido.INTRODUZIDO && pedido.getStatus() != StatusPedido.EM_ANALISE) {
            throw new RegraNegocioException(HttpStatus.CONFLICT, "Este pedido já foi avaliado.");
        }
        pedido.setAgenteAvaliador(agenteAtual());
        pedido.setParecer(pedidoAvaliacao.parecer().trim());
        pedido.setStatus("POSITIVO".equals(pedidoAvaliacao.decisao())
                ? StatusPedido.PARECER_POSITIVO
                : StatusPedido.PARECER_NEGATIVO);
        return PedidoResposta.de(pedido);
    }

    public PedidoResposta executar(Long id) {
        exigirAgente();
        Pedido pedido = carregarVisivel(id);
        if (pedido.getStatus() != StatusPedido.PARECER_POSITIVO) {
            throw new RegraNegocioException(HttpStatus.CONFLICT, "O contrato só começa depois de um parecer positivo.");
        }
        pedido.setStatus(StatusPedido.CONTRATO_EM_EXECUCAO);
        return PedidoResposta.de(pedido);
    }

    public PedidoResposta contratar(Long id, ContratoRequest pedidoContrato) {
        Agente banco = agenteAtual();
        if (banco.getTipo() != TipoAgente.BANCO) {
            throw new RegraNegocioException(HttpStatus.FORBIDDEN, "Só um banco agente concede o contrato de crédito.");
        }
        Pedido pedido = carregarVisivel(id);
        if (pedido.getStatus() != StatusPedido.PARECER_POSITIVO) {
            throw new RegraNegocioException(HttpStatus.CONFLICT, "O crédito só é associado a um pedido com parecer positivo.");
        }
        if (pedido.getContrato() != null) {
            throw new RegraNegocioException(HttpStatus.CONFLICT, "Este pedido já possui contrato de crédito.");
        }
        ContratoCredito contrato = new ContratoCredito();
        contrato.setPedido(pedido);
        contrato.setBanco(banco);
        contrato.setValorFinanciado(pedidoContrato.valorFinanciado());
        contrato.setNumeroParcelas(pedidoContrato.numeroParcelas());
        contrato.setTaxaMensal(pedidoContrato.taxaMensal());
        contrato.setConcedidoEm(LocalDate.now());
        pedido.setContrato(contrato);
        pedido.setStatus(StatusPedido.CONTRATO_EM_EXECUCAO);
        pedidos.save(pedido);
        return PedidoResposta.de(pedidos.buscar(pedido.getId()).orElse(pedido));
    }

    private void aplicar(Pedido pedido, PedidoRequest dados, Long ignorarId) {
        long dias = Periodo.dias(dados.dataInicio(), dados.dataFim());
        Automovel automovel = automoveis.buscar(dados.automovelId())
                .orElseThrow(() -> new RegraNegocioException(HttpStatus.NOT_FOUND, "Automóvel não encontrado."));
        if (!pedidos.conflitos(automovel.getId(), OCUPA_CARRO, dados.dataInicio(), dados.dataFim(), ignorarId).isEmpty()) {
            throw new RegraNegocioException(HttpStatus.CONFLICT, "Este automóvel já está reservado nesse período.");
        }
        pedido.setAutomovel(automovel);
        pedido.setDataInicio(dados.dataInicio());
        pedido.setDataFim(dados.dataFim());
        pedido.setValorDiaria(dados.valorDiaria());
        pedido.setValorTotal(dados.valorDiaria().multiply(BigDecimal.valueOf(dias)));
        pedido.setObservacao(dados.observacao() == null ? null : dados.observacao().trim());
    }

    private Pedido carregarVisivel(Long id) {
        Pedido pedido = pedidos.buscar(id)
                .orElseThrow(() -> new RegraNegocioException(HttpStatus.NOT_FOUND, "Pedido não encontrado."));
        Sessao sessao = sessaoAtual.exigir();
        if (sessao.getPapel() == Papel.AGENTE || pedido.getCliente().getUsuario().getId().equals(sessao.getId())) {
            return pedido;
        }
        throw new RegraNegocioException(HttpStatus.FORBIDDEN, "Você não pode consultar este pedido.");
    }

    private Cliente clienteAtual() {
        Sessao sessao = sessaoAtual.exigir();
        if (sessao.getPapel() != Papel.CLIENTE) {
            throw new RegraNegocioException(HttpStatus.FORBIDDEN, "Apenas clientes fazem este pedido.");
        }
        return clientes.findByUsuarioId(sessao.getId())
                .orElseThrow(() -> new RegraNegocioException(HttpStatus.CONFLICT, "Complete o cadastro de cliente antes de abrir um pedido."));
    }

    private Agente agenteAtual() {
        exigirAgente();
        return agentes.findByUsuarioId(sessaoAtual.exigir().getId())
                .orElseThrow(() -> new RegraNegocioException(HttpStatus.CONFLICT, "Agente sem cadastro."));
    }

    private void exigirAgente() {
        if (sessaoAtual.exigir().getPapel() != Papel.AGENTE) {
            throw new RegraNegocioException(HttpStatus.FORBIDDEN, "Apenas agentes avaliam pedidos.");
        }
    }
}
