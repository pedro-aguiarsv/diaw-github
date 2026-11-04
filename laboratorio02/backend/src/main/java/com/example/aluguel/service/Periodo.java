package com.example.aluguel.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.http.HttpStatus;

public final class Periodo {

    private Periodo() {
    }

    public static long dias(LocalDate inicio, LocalDate fim) {
        if (inicio == null || fim == null || fim.isBefore(inicio)) {
            throw new RegraNegocioException(HttpStatus.BAD_REQUEST, "A data final não pode ser anterior à inicial.");
        }
        long dias = ChronoUnit.DAYS.between(inicio, fim);
        return dias == 0 ? 1 : dias;
    }
}
