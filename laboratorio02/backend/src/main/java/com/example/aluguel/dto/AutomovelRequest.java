package com.example.aluguel.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AutomovelRequest(
        @NotBlank @Size(max = 20) String matricula,
        @NotNull @Min(1980) @Max(2035) Integer ano,
        @NotBlank @Size(max = 60) String marca,
        @NotBlank @Size(max = 60) String modelo,
        @NotBlank @Size(max = 8) String placa
) {
}
