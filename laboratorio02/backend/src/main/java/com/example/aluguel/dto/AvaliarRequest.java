package com.example.aluguel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AvaliarRequest(
        @NotBlank @Pattern(regexp = "POSITIVO|NEGATIVO") String decisao,
        @NotBlank @Size(max = 500) String parecer
) {
}
