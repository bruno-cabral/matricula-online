package com.matriculaonline.dto.request;

import com.matriculaonline.validation.Cpf;
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
        @Cpf
        String cpf,

        @NotNull(message = "Data de nascimento e obrigatoria")
        LocalDate dataNascimento
) {}
