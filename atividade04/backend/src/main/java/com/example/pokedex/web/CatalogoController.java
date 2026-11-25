package com.example.pokedex.web;

import com.example.pokedex.model.PokemonType;
import com.example.pokedex.model.Trainer;
import com.example.pokedex.repository.PokemonTypeRepository;
import com.example.pokedex.repository.TrainerRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CatalogoController {

    private final PokemonTypeRepository tipos;
    private final TrainerRepository treinadores;

    public CatalogoController(PokemonTypeRepository tipos, TrainerRepository treinadores) {
        this.tipos = tipos;
        this.treinadores = treinadores;
    }

    @GetMapping("/types")
    public List<PokemonType> tipos() {
        return tipos.findAll().stream().sorted(Comparator.comparing(PokemonType::getId)).toList();
    }

    @GetMapping("/trainers")
    public List<Trainer> treinadores() {
        return treinadores.findAll().stream().sorted(Comparator.comparing(Trainer::getId)).toList();
    }
}
