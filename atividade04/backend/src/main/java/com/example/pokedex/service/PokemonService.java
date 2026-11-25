package com.example.pokedex.service;

import com.example.pokedex.model.Pokemon;
import com.example.pokedex.model.PokemonType;
import com.example.pokedex.model.Stats;
import com.example.pokedex.model.Trainer;
import com.example.pokedex.model.TypeRef;
import com.example.pokedex.repository.PokemonRepository;
import com.example.pokedex.repository.PokemonTypeRepository;
import com.example.pokedex.repository.TrainerRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class PokemonService {

    private final PokemonRepository pokemons;
    private final PokemonTypeRepository tipos;
    private final TrainerRepository treinadores;

    public PokemonService(PokemonRepository pokemons, PokemonTypeRepository tipos, TrainerRepository treinadores) {
        this.pokemons = pokemons;
        this.tipos = tipos;
        this.treinadores = treinadores;
    }

    public List<Pokemon> listar(String nome, String tipo) {
        String busca = nome == null ? "" : nome.trim().toLowerCase().replace("#", "");
        String filtroTipo = tipo == null ? "" : tipo.trim();
        return pokemons.findAll().stream()
                .filter(pokemon -> busca.isEmpty() || corresponde(pokemon, busca))
                .filter(pokemon -> filtroTipo.isEmpty() || possuiTipo(pokemon, filtroTipo))
                .sorted(Comparator.comparing(Pokemon::getId))
                .toList();
    }

    public Pokemon buscar(Integer id) {
        return pokemons.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pokémon não encontrado."));
    }

    public Pokemon criar(Pokemon entrada) {
        preparar(entrada);
        if (entrada.getId() == null) {
            int proximo = pokemons.findAll().stream().mapToInt(Pokemon::getId).max().orElse(0) + 1;
            entrada.setId(proximo);
        } else if (pokemons.existsById(entrada.getId())) {
            throw new ConflitoException("Já existe um Pokémon com esse número.");
        }
        return pokemons.save(entrada);
    }

    public Pokemon atualizar(Integer id, Pokemon entrada) {
        buscar(id);
        preparar(entrada);
        entrada.setId(id);
        return pokemons.save(entrada);
    }

    public void excluir(Integer id) {
        buscar(id);
        pokemons.deleteById(id);
        for (Trainer treinador : treinadores.findAll()) {
            boolean mudou = treinador.getPokemonIds().removeIf(id::equals);
            if (mudou) {
                treinadores.save(treinador);
            }
        }
    }

    private void preparar(Pokemon entrada) {
        if (entrada.getName() == null || entrada.getName().isBlank()) {
            throw new RegraNegocioException("Informe o nome.");
        }
        entrada.setName(entrada.getName().trim());
        if (entrada.getName().length() > 40) {
            throw new RegraNegocioException("O nome pode ter no máximo 40 caracteres.");
        }
        validarAtributos(entrada.getStats());
        entrada.setTypes(normalizarTipos(entrada.getTypes()));
    }

    private void validarAtributos(Stats stats) {
        if (stats == null) {
            throw new RegraNegocioException("Informe os atributos.");
        }
        int[] valores = {
                stats.getHp(), stats.getAttack(), stats.getDefense(),
                stats.getSpecialAttack(), stats.getSpecialDefense(), stats.getSpeed()
        };
        for (int valor : valores) {
            if (valor < 1 || valor > 255) {
                throw new RegraNegocioException("Cada atributo deve ficar entre 1 e 255.");
            }
        }
    }

    private List<TypeRef> normalizarTipos(List<TypeRef> types) {
        if (types == null || types.isEmpty()) {
            return List.of();
        }
        List<TypeRef> saida = new ArrayList<>();
        for (TypeRef tipo : types) {
            PokemonType catalogo = null;
            if (tipo.getId() != null) {
                catalogo = tipos.findById(tipo.getId()).orElse(null);
            }
            if (catalogo == null && tipo.getName() != null && !tipo.getName().isBlank()) {
                catalogo = tipos.findByNameIgnoreCase(tipo.getName().trim()).orElse(null);
            }
            if (catalogo == null) {
                throw new RegraNegocioException("Tipo desconhecido.");
            }
            Integer id = catalogo.getId();
            boolean repetido = saida.stream().anyMatch(item -> item.getId().equals(id));
            if (!repetido) {
                saida.add(new TypeRef(id, catalogo.getName()));
            }
        }
        if (saida.size() > 2) {
            throw new RegraNegocioException("Um Pokémon pode ter no máximo dois tipos.");
        }
        return saida;
    }

    private boolean corresponde(Pokemon pokemon, String busca) {
        return pokemon.getName().toLowerCase().contains(busca)
                || String.valueOf(pokemon.getId()).contains(busca)
                || String.format("%03d", pokemon.getId()).contains(busca);
    }

    private boolean possuiTipo(Pokemon pokemon, String tipo) {
        return pokemon.getTypes().stream().anyMatch(item -> item.getName().equalsIgnoreCase(tipo));
    }
}
