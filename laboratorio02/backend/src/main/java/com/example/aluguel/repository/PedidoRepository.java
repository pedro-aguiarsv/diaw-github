package com.example.aluguel.repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.aluguel.model.Pedido;
import com.example.aluguel.model.StatusPedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("""
            select distinct p from Pedido p
            join fetch p.cliente c
            join fetch c.usuario
            left join fetch c.empregadores
            join fetch p.automovel
            left join fetch p.agenteAvaliador
            left join fetch p.contrato ct
            left join fetch ct.banco
            where c.id = :clienteId
            order by p.criadoEm desc
            """)
    List<Pedido> doCliente(Long clienteId);

    @Query("""
            select distinct p from Pedido p
            join fetch p.cliente c
            join fetch c.usuario
            left join fetch c.empregadores
            join fetch p.automovel
            left join fetch p.agenteAvaliador
            left join fetch p.contrato ct
            left join fetch ct.banco
            order by p.criadoEm desc
            """)
    List<Pedido> listar();

    @Query("""
            select p from Pedido p
            join fetch p.cliente c
            join fetch c.usuario
            left join fetch c.empregadores
            join fetch p.automovel
            left join fetch p.agenteAvaliador
            left join fetch p.contrato ct
            left join fetch ct.banco
            where p.id = :id
            """)
    Optional<Pedido> buscar(Long id);

    @Query("""
            select p from Pedido p
            where p.automovel.id = :automovelId
              and p.status in :status
              and p.dataInicio <= :fim
              and p.dataFim >= :inicio
              and (:ignorar is null or p.id <> :ignorar)
            """)
    List<Pedido> conflitos(Long automovelId, Collection<StatusPedido> status, LocalDate inicio, LocalDate fim, Long ignorar);

    @Query("""
            select count(p) from Pedido p
            where p.cliente.id = :clienteId
              and p.status not in :encerrados
            """)
    long abertosDoCliente(Long clienteId, Collection<StatusPedido> encerrados);

    @Query("""
            select count(p) from Pedido p
            where p.automovel.id = :automovelId
              and p.status not in :encerrados
            """)
    long abertosDoAutomovel(Long automovelId, Collection<StatusPedido> encerrados);
}
