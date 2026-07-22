package com.clinica.odontologica.Exception;

public class DuplicatedRecepcionistaException extends RuntimeException {

    public DuplicatedRecepcionistaException(String message) {
        super(message);
    }

    public DuplicatedRecepcionistaException(String message, Throwable cause) {
        super(message, cause);
    }
}
