package com.innovaschools.asistec.domain.exception;

public class TooManyAttemptsException extends RuntimeException {
    public TooManyAttemptsException() {
        super("Demasiados intentos. Probá de nuevo en unos minutos");
    }
}
