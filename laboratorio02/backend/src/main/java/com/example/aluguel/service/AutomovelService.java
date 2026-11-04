package com.example.aluguel.service;

import java.time.Year;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.aluguel.dto.AutomovelRequest;
import com.example.aluguel.dto.AutomovelResposta;
import com.example.aluguel.model.Agente;
import com.example.aluguel.model.Automovel;
import com.example.aluguel.model.Cliente;
import com.example.aluguel.model.Papel;
import com.example.aluguel.model.StatusPedido;
import com.example.aluguel.model.TipoProprietario;
import com.example.aluguel.repository.AgenteRepository;
import com.example.aluguel.repository.AutomovelRepository;
import com.example.aluguel.repository.ClienteRepository;
import com.example.aluguel.repository.PedidoRepository;
import com.example.aluguel.security.Sessao;
import com.example.aluguel.security.SessaoAtual;

@Service
@Transactional
public class AutomovelService {

    private static final List<StatusPedido> ENCERRADOS = List.of(StatusPedido.CANCELADO, StatusPedido.PARECER_NEGATIVO);

    private final AutomovelRepository automoveis;
    private final ClienteRepository clientes;
    private final AgenteRepository agentes;
    private final PedidoRepository pedidos;
    private final SessaoAtual sessaoAtual;

    public AutomovelService(
            AutomovelRepository automoveis,
            ClienteRepository clientes,
            AgenteRepository agentes,
            PedidoRepository pedidos,
            SessaoAtual sessaoAtual) {
        this.automoveis = automoveis;
        this.clientes = clientes;
        this.agentes = agentes;
        this.pedidos = pedidos;
        this.sessaoAtual = sessaoAtual;
    }

    @Transactional(readOnly = true)
    public List<AutomovelResposta> listar() {
        Long usuarioId = sessaoAtual.exigir().getId();
        return automoveis.listar().stream()
                .collect(Collectors.toMap(Automovel::getId, Function.identity(), (a, b) -> a, LinkedHashMap::new))
                .values().stream()
                .map(carro -> AutomovelResposta.de(carro, usuarioId))
                .toList();
    }

    public AutomovelResposta criar(AutomovelRequest pedido) {
        Sessao sessao = sessaoAtual.exigir();
        Automovel automovel = new Automovel();
        aplicar(automovel, pedido, null);
        vincularDono(automovel, sessao);
        return AutomovelResposta.de(automoveis.save(automovel), sessao.getId());
    }

    public AutomovelResposta atualizar(Long id, AutomovelRequest pedido) {
        Sessao sessao = sessaoAtual.exigir();
        Automovel automovel = carregarDoDono(id, sessao);
        aplicar(automovel, pedido, automovel.getId());
        return AutomovelResposta.de(automovel, sessao.getId());
    }

    public void remover(Long id) {
        Automovel automovel = carregarDoDono(id, sessaoAtual.exigir());
        if (pedidos.abertosDoAutomovel(automovel.getId(), ENCERRADOS) > 0) {
            throw new RegraNegocioException(HttpStatus.CONFLICT, "Este automóvel está ligado a um pedido em aberto.");
        }
        automoveis.delete(automovel);
    }

    private void vincularDono(Automovel automovel, Sessao sessao) {
        if (sessao.getPapel() == Papel.CLIENTE) {
            Cliente cliente = clientes.findByUsuarioId(sessao.getId())
                    .orElseThrow(() -> new RegraNegocioException(HttpStatus.CONFLICT, "Complete o cadastro de cliente antes de registrar um automóvel."));
            automovel.setTipoProprietario(TipoProprietario.CLIENTE);
            automovel.setProprietarioCliente(cliente);
            automovel.setProprietarioAgente(null);
            return;
        }
        Agente agente = agentes.findByUsuarioId(sessao.getId())
                .orElseThrow(() -> new RegraNegocioException(HttpStatus.CONFLICT, "Agente sem cadastro."));
        automovel.setTipoProprietario(TipoProprietario.valueOf(agente.getTipo().name()));
        automovel.setProprietarioAgente(agente);
        automovel.setProprietarioCliente(null);
    }

    private Automovel carregarDoDono(Long id, Sessao sessao) {
        Automovel automovel = automoveis.buscar(id)
                .orElseThrow(() -> new RegraNegocioException(HttpStatus.NOT_FOUND, "Automóvel não encontrado."));
        boolean dono = automovel.getProprietarioCliente() != null
                ? automovel.getProprietarioCliente().getUsuario().getId().equals(sessao.getId())
                : automovel.getProprietarioAgente() != null
                && automovel.getProprietarioAgente().getUsuario().getId().equals(sessao.getId());
        if (!dono) {
            throw new RegraNegocioException(HttpStatus.FORBIDDEN, "Só o proprietário altera este automóvel.");
        }
        return automovel;
    }

    private void aplicar(Automovel automovel, AutomovelRequest pedido, Long ignorarId) {
        int anoLimite = Year.now().getValue() + 1;
        if (pedido.ano() > anoLimite) {
            throw new RegraNegocioException(HttpStatus.BAD_REQUEST, "Ano do automóvel inválido.");
        }
        String placa = normalizarPlaca(pedido.placa());
        String matricula = pedido.matricula().trim().toUpperCase();
        boolean placaEmUso = ignorarId == null
                ? automoveis.existsByPlaca(placa)
                : automoveis.existsByPlacaAndIdNot(placa, ignorarId);
        boolean matriculaEmUso = ignorarId == null
                ? automoveis.existsByMatricula(matricula)
                : automoveis.existsByMatriculaAndIdNot(matricula, ignorarId);
        if (placaEmUso) {
            throw new RegraNegocioException(HttpStatus.CONFLICT, "Placa já cadastrada.");
        }
        if (matriculaEmUso) {
            throw new RegraNegocioException(HttpStatus.CONFLICT, "Matrícula já cadastrada.");
        }
        automovel.setMatricula(matricula);
        automovel.setAno(pedido.ano());
        automovel.setMarca(pedido.marca().trim());
        automovel.setModelo(pedido.modelo().trim());
        automovel.setPlaca(placa);
    }

    static String normalizarPlaca(String placa) {
        String limpa = placa.toUpperCase().replaceAll("[^A-Z0-9]", "");
        boolean antiga = limpa.matches("[A-Z]{3}[0-9]{4}");
        boolean mercosul = limpa.matches("[A-Z]{3}[0-9][A-Z0-9][0-9]{2}");
        if (!antiga && !mercosul) {
            throw new RegraNegocioException(HttpStatus.BAD_REQUEST, "Placa inválida. Use o padrão antigo ou Mercosul.");
        }
        return limpa;
    }
}
