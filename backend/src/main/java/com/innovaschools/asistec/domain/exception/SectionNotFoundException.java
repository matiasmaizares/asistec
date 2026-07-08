package com.innovaschools.asistec.domain.exception;

import java.util.UUID;

public class SectionNotFoundException extends RuntimeException {
    public SectionNotFoundException(UUID sectionId) {
        super("No existe la sección con id: " + sectionId);
    }
}
