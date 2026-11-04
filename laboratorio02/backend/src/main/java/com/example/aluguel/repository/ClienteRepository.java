package com.example.aluguel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.aluguel.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    @Query("select distinct c from Cliente c join fetch c.usuario left join fetch c.empregadores order by c.nome")
    List<Cliente> listar();

    @Query("select c from Cliente c join fetch c.usuario left join fetch c.empregadores where c.id = :id")
    Optional<Cliente> buscar(Long id);

    @Query("select c from Cliente c join fetch c.usuario left join fetch c.empregadores where c.usuario.id = :usuarioId")
    Optional<Cliente> findByUsuarioId(Long usuarioId);

    boolean existsByCpf(String cpf);

    boolean existsByCpfAndIdNot(String cpf, Long id);
}
