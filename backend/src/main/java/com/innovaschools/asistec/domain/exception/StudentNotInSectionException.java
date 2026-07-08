package com.innovaschools.asistec.domain.exception;

import java.util.UUID;

public class StudentNotInSectionException extends RuntimeException {
    public StudentNotInSectionException(UUID studentId, UUID sectionId) {
        super("El estudiante " + studentId + " no pertenece a la sección " + sectionId);
    }
}
