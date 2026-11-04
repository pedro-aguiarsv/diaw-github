package com.example.aluguel.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.aluguel.dto.ClienteRequest;
import com.example.aluguel.dto.ClienteResposta;
import com.example.aluguel.model.Cliente;
import com.example.aluguel.model.Empregador;
import com.example.aluguel.model.Papel;
import com.example.aluguel.model.StatusPedido;
import com.example.aluguel.model.Usuario;
import com.example.aluguel.repository.ClienteRepository;
import com.example.aluguel.repository.PedidoRepository;
import com.example.aluguel.repository.UsuarioRepository;
import com.example.aluguel.security.Sessao;
import com.example.aluguel.security.SessaoAtual;

@Service
@Transactional
public class ClienteService {

    private static final List<StatusPedido> ENCERRADOS = List.of(StatusPedido.CANCELADO, StatusPedido.PARECER_NEGATIVO);

    private final ClienteRepository clientes;
    private final UsuarioRepository usuarios;
    private final PedidoRepository pedidos;
    private final SessaoAtual sessaoAtual;

    public ClienteService(
            ClienteRepository clientes,
            UsuarioRepository usuarios,
            PedidoRepository pedidos,
            SessaoAtual sessaoAtual) {
        this.clientes = clientes;
        this.usuarios = usuarios;
        this.pedidos = pedidos;
        this.sessaoAtual = sessaoAtual;
    }

    public List<ClienteResposta> listar() {
        Sessao sessao = sessaoAtual.exigir();
        if (sessao.getPapel() == Papel.AGENTE) {
            return clientes.listar().stream()
                    .collect(Collectors.toMap(Cliente::getId, Function.identity(), (a, b) -> a, LinkedHashMap::new))
                    .values().stream()
                    .map(ClienteResposta::de)
                    .toList();
        }
        return clientes.findByUsuarioId(sessao.getId()).map(ClienteResposta::de).stream().toList();
    }

    @Transactional(readOnly = true)
    public ClienteResposta me() {
        Sessao sessao = exigirCliente();
        return clientes.findByUsuarioId(sessao.getId())
                .map(ClienteResposta::de)
                .orElseThrow(() -> new RegraNegocioException(HttpStatus.NOT_FOUND, "Cadastro de cliente ainda não existe."));
    }

    public ClienteResposta buscar(Long id) {
        return ClienteResposta.de(carregarVisivel(id));
    }

    public ClienteResposta criar(ClienteRequest pedido) {
        Sessao sessao = exigirCliente();
        if (clientes.findByUsuarioId(sessao.getId()).isPresent()) {
            throw new RegraNegocioException(HttpStatus.CONFLICT, "Este usuário já possui cadastro de cliente.");
        }
        Usuario usuario = usuarios.findById(sessao.getId())
                .orElseThrow(() -> new RegraNegocioException(HttpStatus.UNAUTHORIZED, "Faça login para continuar."));
        Cliente cliente = new Cliente();
        cliente.setUsuario(usuario);
        aplicar(cliente, pedido, null);
        return ClienteResposta.de(clientes.save(cliente));
    }

    public ClienteResposta atualizar(Long id, ClienteRequest pedido) {
        Cliente cliente = carregarVisivel(id);
        if (sessaoAtual.exigir().getPapel() != Papel.CLIENTE || !cliente.getUsuario().getId().equals(sessaoAtual.exigir().getId())) {
            throw new RegraNegocioException(HttpStatus.FORBIDDEN, "Só o próprio cliente altera o cadastro.");
        }
        aplicar(cliente, pedido, cliente.getId());
        return ClienteResposta.de(cliente);
    }

    public void remover(Long id) {
        Cliente cliente = carregarVisivel(id);
        if (!cliente.getUsuario().getId().equals(sessaoAtual.exigir().getId())) {
            throw new RegraNegocioException(HttpStatus.FORBIDDEN, "Só o próprio cliente remove o cadastro.");
        }
        if (pedidos.abertosDoCliente(cliente.getId(), ENCERRADOS) > 0) {
            throw new RegraNegocioException(HttpStatus.CONFLICT, "Cancele os pedidos em aberto antes de remover o cadastro.");
        }
        clientes.delete(cliente);
    }

    private Cliente carregarVisivel(Long id) {
        Cliente cliente = clientes.buscar(id)
                .orElseThrow(() -> new RegraNegocioException(HttpStatus.NOT_FOUND, "Cliente não encontrado."));
        Sessao sessao = sessaoAtual.exigir();
        if (sessao.getPapel() == Papel.AGENTE || cliente.getUsuario().getId().equals(sessao.getId())) {
            return cliente;
        }
        throw new RegraNegocioException(HttpStatus.FORBIDDEN, "Você não pode consultar este cliente.");
    }

    private Sessao exigirCliente() {
        Sessao sessao = sessaoAtual.exigir();
        if (sessao.getPapel() != Papel.CLIENTE) {
            throw new RegraNegocioException(HttpStatus.FORBIDDEN, "Apenas clientes possuem este cadastro.");
        }
        return sessao;
    }

    private void aplicar(Cliente cliente, ClienteRequest pedido, Long ignorarId) {
        String cpf = Cpf.normalizar(pedido.cpf());
        boolean cpfEmUso = ignorarId == null ? clientes.existsByCpf(cpf) : clientes.existsByCpfAndIdNot(cpf, ignorarId);
        if (cpfEmUso) {
            throw new RegraNegocioException(HttpStatus.CONFLICT, "CPF já cadastrado.");
        }
        List<ClienteRequest.EmpregadorItem> rendas = pedido.empregadores() == null ? List.of() : pedido.empregadores();
        if (rendas.size() > 3) {
            throw new RegraNegocioException(HttpStatus.BAD_REQUEST, "Um cliente pode ter no máximo 3 entidades empregadoras.");
        }
        cliente.setRg(pedido.rg().trim());
        cliente.setCpf(cpf);
        cliente.setNome(pedido.nome().trim());
        cliente.setEndereco(pedido.endereco().trim());
        cliente.setProfissao(pedido.profissao().trim());
        cliente.getEmpregadores().clear();
        for (ClienteRequest.EmpregadorItem item : rendas) {
            Empregador empregador = new Empregador();
            empregador.setCliente(cliente);
            empregador.setEntidade(item.entidade().trim());
            empregador.setRendimento(item.rendimento());
            cliente.getEmpregadores().add(empregador);
        }
    }
}
