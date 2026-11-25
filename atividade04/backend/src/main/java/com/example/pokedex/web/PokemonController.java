package com.example.pokedex.web;

import com.example.pokedex.model.Pokemon;
import com.example.pokedex.service.PokemonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pokemons")
public class PokemonController {

    private final PokemonService service;

    public PokemonController(PokemonService service) {
        this.service = service;
    }

    @GetMapping
    public List<Pokemon> listar(@RequestParam(required = false) String nome,
                                @RequestParam(required = false) String tipo) {
        return service.listar(nome, tipo);
    }

    @GetMapping("/{id}")
    public Pokemon buscar(@PathVariable Integer id) {
        return service.buscar(id);
    }

    @PostMapping
    public ResponseEntity<Pokemon> criar(@RequestBody Pokemon pokemon) {
        return ResponseEntity.status(201).body(service.criar(pokemon));
    }

    @PutMapping("/{id}")
    public Pokemon atualizar(@PathVariable Integer id, @RequestBody Pokemon pokemon) {
        return service.atualizar(id, pokemon);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
