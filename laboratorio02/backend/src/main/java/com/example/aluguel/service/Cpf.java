package com.example.aluguel.service;

import org.springframework.http.HttpStatus;

public final class Cpf {

    private Cpf() {
    }

    public static String normalizar(String valor) {
        if (valor == null) {
            throw new RegraNegocioException(HttpStatus.BAD_REQUEST, "CPF inválido.");
        }
        String digitos = valor.replaceAll("\\D", "");
        if (!valido(digitos)) {
            throw new RegraNegocioException(HttpStatus.BAD_REQUEST, "CPF inválido.");
        }
        return digitos;
    }

    public static boolean valido(String digitos) {
        if (digitos == null || !digitos.matches("\\d{11}")) {
            return false;
        }
        if (digitos.chars().distinct().count() == 1) {
            return false;
        }
        return digito(digitos, 9) == Character.getNumericValue(digitos.charAt(9))
                && digito(digitos, 10) == Character.getNumericValue(digitos.charAt(10));
    }

    private static int digito(String digitos, int tamanho) {
        int soma = 0;
        for (int i = 0; i < tamanho; i++) {
            soma += Character.getNumericValue(digitos.charAt(i)) * (tamanho + 1 - i);
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
