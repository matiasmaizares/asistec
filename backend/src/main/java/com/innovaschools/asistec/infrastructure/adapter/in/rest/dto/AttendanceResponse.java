package com.innovaschools.asistec.infrastructure.adapter.in.rest.dto;

import java.time.LocalDateTime;

public record AttendanceResponse(String message, LocalDateTime savedAt) {

    public static AttendanceResponse created(LocalDateTime savedAt) {
        return new AttendanceResponse("Asistencia registrada correctamente", savedAt);
    }

    public static AttendanceResponse updated(LocalDateTime savedAt) {
        return new AttendanceResponse("Asistencia actualizada correctamente", savedAt);
    }
}
