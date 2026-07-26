package com.matriculaonline.dto.request;

import jakarta.validation.constraints.*;

public record CursoRequest(
        @NotBlank(message = "Nome e obrigatorio")
        @Size(max = 255, message = "Nome deve ter no maximo 255 caracteres")
        String nome,

        String descricao,

        @NotNull(message = "Carga horaria e obrigatoria")
        @Min(value = 1, message = "Carga horaria deve ser maior que zero")
        Integer cargaHoraria
) {}
