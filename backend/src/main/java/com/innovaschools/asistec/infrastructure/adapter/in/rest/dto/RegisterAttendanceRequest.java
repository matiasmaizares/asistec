package com.innovaschools.asistec.infrastructure.adapter.in.rest.dto;

import com.innovaschools.asistec.domain.model.AttendanceStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record RegisterAttendanceRequest(
        @NotNull UUID sectionId,
        @NotNull LocalDate date,
        @NotEmpty @Valid List<StudentStatusRequest> records
) {
    public record StudentStatusRequest(
            @NotNull UUID studentId,
            @NotNull AttendanceStatus status
    ) {}
}
