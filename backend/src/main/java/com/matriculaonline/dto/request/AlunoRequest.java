package com.matriculaonline.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record AlunoRequest(
        @NotBlank(message = "Nome e obrigatorio")
        @Size(max = 255, message = "Nome deve ter no maximo 255 caracteres")
        String nome,

        @NotBlank(message = "Email e obrigatorio")
        @Email(message = "Email deve ser valido")
        String email,

        @NotBlank(message = "CPF e obrigatorio")
        @Size(min = 11, max = 14, message = "CPF deve ter entre 11 e 14 caracteres")
        String cpf,

        @NotNull(message = "Data de nascimento e obrigatoria")
        LocalDate dataNascimento
) {}
