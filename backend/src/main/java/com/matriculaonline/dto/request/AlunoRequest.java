package com.matriculaonline.dto.request;

import com.matriculaonline.validation.Cpf;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record AlunoRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
        String nome,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email deve ser válido")
        String email,

        @NotBlank(message = "CPF é obrigatório")
        @Cpf
        String cpf,

        @NotNull(message = "Data de nascimento é obrigatória")
        LocalDate dataNascimento
) {}
