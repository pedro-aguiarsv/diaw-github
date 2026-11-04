package com.example.aluguel.dto;

import com.example.aluguel.model.Papel;
import com.example.aluguel.model.TipoAgente;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistrarRequest(
        @NotBlank @Size(min = 3, max = 40) String usuario,
        @NotBlank @Size(min = 6, max = 80) String senha,
        @NotBlank @Size(max = 120) String nome,
        @NotNull Papel papel,
        TipoAgente tipoAgente,
        @Size(max = 160) String razaoSocial
) {
}
