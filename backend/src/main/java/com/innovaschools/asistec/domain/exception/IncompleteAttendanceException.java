package com.innovaschools.asistec.domain.exception;

public class IncompleteAttendanceException extends RuntimeException {
    public IncompleteAttendanceException(int expected, int received) {
        super("El registro debe incluir todos los alumnos de la sección. Esperados: "
              + expected + ", recibidos: " + received);
    }
}
