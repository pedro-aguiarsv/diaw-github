package com.example.pokedex.web;

import com.example.pokedex.service.ConflitoException;
import com.example.pokedex.service.RecursoNaoEncontradoException;
import com.example.pokedex.service.RegraNegocioException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    ResponseEntity<Map<String, String>> naoEncontrado(RecursoNaoEncontradoException ex) {
        return ResponseEntity.status(404).body(Map.of("mensagem", ex.getMessage()));
    }

    @ExceptionHandler(ConflitoException.class)
    ResponseEntity<Map<String, String>> conflito(ConflitoException ex) {
        return ResponseEntity.status(409).body(Map.of("mensagem", ex.getMessage()));
    }

    @ExceptionHandler(RegraNegocioException.class)
    ResponseEntity<Map<String, String>> regra(RegraNegocioException ex) {
        return ResponseEntity.badRequest().body(Map.of("mensagem", ex.getMessage()));
    }
}
