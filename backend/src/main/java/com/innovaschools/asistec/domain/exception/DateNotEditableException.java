package com.innovaschools.asistec.domain.exception;

public class DateNotEditableException extends RuntimeException {
    public DateNotEditableException() {
        super("Solo se puede registrar asistencia para el día actual");
    }
}
