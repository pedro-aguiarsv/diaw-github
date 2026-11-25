package com.example.pokedex.config;

import com.example.pokedex.model.Pokemon;
import com.example.pokedex.model.PokemonType;
import com.example.pokedex.model.Trainer;
import com.example.pokedex.repository.PokemonRepository;
import com.example.pokedex.repository.PokemonTypeRepository;
import com.example.pokedex.repository.TrainerRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@Configuration
public class CargaInicial {

    @Bean
    ApplicationRunner popular(PokemonRepository pokemons, PokemonTypeRepository tipos, TrainerRepository treinadores, JsonMapper json) {
        return args -> {
            if (pokemons.count() > 0) {
                return;
            }
            ClassPathResource arquivo = new ClassPathResource("seed/pokedex.json");
            Seed seed = json.readValue(arquivo.getInputStream(), Seed.class);
            tipos.saveAll(seed.types());
            pokemons.saveAll(seed.pokemon());
            treinadores.saveAll(seed.trainers());
        };
    }

    public record Seed(List<PokemonType> types, List<Pokemon> pokemon, List<Trainer> trainers) {
    }
}
