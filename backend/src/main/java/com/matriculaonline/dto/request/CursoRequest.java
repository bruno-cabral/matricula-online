package com.matriculaonline.dto.request;

import jakarta.validation.constraints.*;

public record CursoRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
        String nome,

        String descricao,

        @NotNull(message = "Carga horária é obrigatória")
        @Min(value = 1, message = "Carga horária deve ser maior que zero")
        Integer cargaHoraria
) {}
