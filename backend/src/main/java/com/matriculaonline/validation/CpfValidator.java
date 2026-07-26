package com.matriculaonline.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfValidator implements ConstraintValidator<Cpf, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true; // @NotBlank cuida do obrigatório
        }
        return isCpfValido(value);
    }

    /**
     * Valida CPF com dígitos verificadores.
     * Aceita formatado (000.000.000-00) ou apenas dígitos.
     */
    static boolean isCpfValido(String cpf) {
        String digits = cpf.replaceAll("\\D", "");

        if (digits.length() != 11) {
            return false;
        }

        if (digits.chars().distinct().count() == 1) {
            return false;
        }

        int digito1 = calcularDigito(digits.substring(0, 9), 10);
        int digito2 = calcularDigito(digits.substring(0, 9) + digito1, 11);

        return digits.equals(digits.substring(0, 9) + digito1 + digito2);
    }

    private static int calcularDigito(String base, int pesoInicial) {
        int soma = 0;
        for (int i = 0; i < base.length(); i++) {
            soma += Character.getNumericValue(base.charAt(i)) * (pesoInicial - i);
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
