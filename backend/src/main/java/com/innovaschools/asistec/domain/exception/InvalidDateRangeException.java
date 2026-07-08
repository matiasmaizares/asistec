package com.innovaschools.asistec.domain.exception;

public class InvalidDateRangeException extends RuntimeException {
    public InvalidDateRangeException() {
        super("La fecha de inicio no puede ser posterior a la fecha de fin");
    }
}
