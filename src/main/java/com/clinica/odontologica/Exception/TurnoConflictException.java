package com.clinica.odontologica.Exception;

public class TurnoConflictException extends RuntimeException {

    public TurnoConflictException(String message) {
        super(message);
    }

    public TurnoConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
