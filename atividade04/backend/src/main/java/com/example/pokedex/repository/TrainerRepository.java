package com.example.pokedex.repository;

import com.example.pokedex.model.Trainer;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TrainerRepository extends MongoRepository<Trainer, Integer> {
}
