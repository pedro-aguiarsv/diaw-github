package com.example.aluguel.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ClienteRequest(
        @NotBlank @Size(min = 4, max = 20) String rg,
        @NotBlank String cpf,
        @NotBlank @Size(min = 3, max = 120) String nome,
        @NotBlank @Size(max = 200) String endereco,
        @NotBlank @Size(max = 80) String profissao,
        @Size(max = 3) List<@Valid EmpregadorItem> empregadores
) {
    public record EmpregadorItem(
            @NotBlank @Size(max = 120) String entidade,
            @NotNull @DecimalMin("0.0") BigDecimal rendimento
    ) {
    }
}
