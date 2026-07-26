package com.matriculaonline.domain.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, String identifier) {
        super(resource + " nao encontrado(a) com identificador: " + identifier);
    }
}
