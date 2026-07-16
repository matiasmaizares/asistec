package com.innovaschools.asistec.infrastructure.event;

import com.innovaschools.asistec.domain.port.out.AttendanceEventPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Implementación del puerto para el perfil "test": evita depender de un Redis
 * real en la suite de tests, haciendo el fan-out local directamente (el
 * comportamiento que tenía el adapter antes de introducir Redis).
 */
@Component
@Profile("test")
public class LocalAttendanceEventPublisher implements AttendanceEventPort {

    private final SseAttendanceEventAdapter sseAdapter;

    public LocalAttendanceEventPublisher(SseAttendanceEventAdapter sseAdapter) {
        this.sseAdapter = sseAdapter;
    }

    @Override
    public void publishAttendanceUpdated(UUID sectionId, LocalDate date) {
        sseAdapter.broadcastLocal(sectionId, date);
    }
}
