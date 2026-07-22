package com.clinica.odontologica.Exception;

public class IllegalArgument extends RuntimeException {

    public IllegalArgument(String message) {
        super(message);
    }

    public IllegalArgument(String message, Throwable cause) {
        super(message, cause);
    }
}
