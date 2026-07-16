package com.innovaschools.asistec.domain.exception;

import java.util.UUID;

public class NotAssignedToSectionException extends RuntimeException {
    public NotAssignedToSectionException(UUID sectionId) {
        super("No tenés asignada la sección " + sectionId);
    }
}
