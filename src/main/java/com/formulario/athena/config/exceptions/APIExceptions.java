package com.formulario.athena.config.exceptions;

public class APIExceptions extends RuntimeException {
    private static final long SerialVersionUID = 1L;

    public APIExceptions() {
    }

    public APIExceptions(String message) {
        super(message);
    }
}
