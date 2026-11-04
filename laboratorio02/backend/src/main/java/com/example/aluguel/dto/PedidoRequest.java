package com.example.aluguel.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PedidoRequest(
        @NotNull Long automovelId,
        @NotNull LocalDate dataInicio,
        @NotNull LocalDate dataFim,
        @NotNull @DecimalMin("1.0") BigDecimal valorDiaria,
        @Size(max = 400) String observacao
) {
}
