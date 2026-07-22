package com.clinica.odontologica.Exception;

public class RecepcionistaNotFoundException extends RuntimeException {

    public RecepcionistaNotFoundException(String message) {
        super(message);
    }

    public RecepcionistaNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
