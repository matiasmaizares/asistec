package com.innovaschools.asistec.infrastructure.adapter.out.persistence.adapter;

import com.innovaschools.asistec.domain.model.AttendanceStatus;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Selecciona cómo se resuelve el upsert de asistencia según el perfil activo:
 * atómico vía SQL nativo en Postgres (PostgresAttendanceUpsertExecutor), o
 * find-then-save vía JPA en el perfil "test" (LocalAttendanceUpsertExecutor,
 * donde H2 no soporta "ON CONFLICT ... DO UPDATE" y no hace falta atomicidad
 * real porque los tests no ejercitan concurrencia).
 */
interface AttendanceUpsertExecutor {
    void upsert(UUID studentId, UUID sectionId, LocalDate date, AttendanceStatus status);
}
