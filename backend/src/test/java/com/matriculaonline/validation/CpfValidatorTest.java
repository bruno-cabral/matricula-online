package com.matriculaonline.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class CpfValidatorTest {

    private final CpfValidator validator = new CpfValidator();

    @ParameterizedTest
    @ValueSource(strings = {
            "52998224725",
            "39053344705",
            "11144477735",
            "12345678909",
            "529.982.247-25"
    })
    @DisplayName("CPFs validos (com e sem mascara) devem ser aceitos")
    void deveAceitarCpfsValidos(String cpf) {
        assertThat(validator.isValid(cpf, null)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "12345678901",
            "99988877766",
            "11111111111",
            "00000000000",
            "123",
            "52998224726",
            "abcdefghijk"
    })
    @DisplayName("CPFs invalidos devem ser rejeitados")
    void deveRejeitarCpfsInvalidos(String cpf) {
        assertThat(validator.isValid(cpf, null)).isFalse();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t"})
    @DisplayName("Null/blank ficam a cargo do @NotBlank")
    void deveIgnorarNullOuBlank(String cpf) {
        assertThat(validator.isValid(cpf, null)).isTrue();
    }

    @Test
    @DisplayName("Algoritmo rejeita digitos verificadores incorretos")
    void deveRejeitarDigitoVerificadorIncorreto() {
        assertThat(CpfValidator.isCpfValido("52998224726")).isFalse();
        assertThat(CpfValidator.isCpfValido("52998224725")).isTrue();
    }
}
