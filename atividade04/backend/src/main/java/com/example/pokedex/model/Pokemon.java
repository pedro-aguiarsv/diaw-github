package com.example.pokedex.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "pokemons")
public class Pokemon {

    @Id
    private Integer id;
    private String name;
    private Stats stats;
    private List<TypeRef> types = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public List<TypeRef> getTypes() {
        return types;
    }

    public void setTypes(List<TypeRef> types) {
        this.types = types == null ? new ArrayList<>() : types;
    }
}
