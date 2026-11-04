package com.example.aluguel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.aluguel.model.Automovel;

public interface AutomovelRepository extends JpaRepository<Automovel, Long> {

    @Query("""
            select distinct a from Automovel a
            left join fetch a.proprietarioCliente c
            left join fetch c.usuario
            left join fetch a.proprietarioAgente g
            left join fetch g.usuario
            order by a.marca, a.modelo
            """)
    List<Automovel> listar();

    @Query("""
            select a from Automovel a
            left join fetch a.proprietarioCliente c
            left join fetch c.usuario
            left join fetch a.proprietarioAgente g
            left join fetch g.usuario
            where a.id = :id
            """)
    Optional<Automovel> buscar(Long id);

    boolean existsByPlaca(String placa);
    boolean existsByMatricula(String matricula);
    boolean existsByPlacaAndIdNot(String placa, Long id);
    boolean existsByMatriculaAndIdNot(String matricula, Long id);
}
