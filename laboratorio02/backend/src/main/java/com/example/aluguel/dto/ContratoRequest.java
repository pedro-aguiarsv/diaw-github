package com.example.aluguel.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ContratoRequest(
        @NotNull @DecimalMin("1.0") BigDecimal valorFinanciado,
        @NotNull @Min(1) @Max(60) Integer numeroParcelas,
        @NotNull @DecimalMin("0.0") BigDecimal taxaMensal
) {
}
