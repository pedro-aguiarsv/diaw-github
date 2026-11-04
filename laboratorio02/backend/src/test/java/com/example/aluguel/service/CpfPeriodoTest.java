package com.example.aluguel.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class CpfPeriodoTest {

    @Test
    void aceitaCpfComDigitosValidos() {
        assertEquals("52998224725", Cpf.normalizar("529.982.247-25"));
        assertTrue(Cpf.valido("39053344705"));
        assertFalse(Cpf.valido("11111111111"));
    }

    @Test
    void contaPeloMenosUmDia() {
        LocalDate hoje = LocalDate.of(2026, 11, 4);
        assertEquals(1, Periodo.dias(hoje, hoje));
        assertEquals(7, Periodo.dias(hoje, hoje.plusDays(7)));
        assertThrows(RegraNegocioException.class, () -> Periodo.dias(hoje, hoje.minusDays(1)));
    }
}
