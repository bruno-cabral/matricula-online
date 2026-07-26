package com.matriculaonline.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ValidationErrorResponse(
        int status,
        String error,
        List<FieldError> details,
        LocalDateTime timestamp
) {
    public record FieldError(String campo, String mensagem) {}

    public static ValidationErrorResponse of(int status, String error, List<FieldError> details) {
        return new ValidationErrorResponse(status, error, details, LocalDateTime.now());
    }
}
