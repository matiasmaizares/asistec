package com.innovaschools.asistec.infrastructure.adapter.out.persistence.adapter;

import com.innovaschools.asistec.domain.model.AttendanceStatus;
import com.innovaschools.asistec.infrastructure.adapter.out.persistence.repository.AttendanceRecordJpaRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
@Profile("!test")
class PostgresAttendanceUpsertExecutor implements AttendanceUpsertExecutor {

    private final AttendanceRecordJpaRepository repository;

    PostgresAttendanceUpsertExecutor(AttendanceRecordJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void upsert(UUID studentId, UUID sectionId, LocalDate date, AttendanceStatus status) {
        repository.upsertAttendance(UUID.randomUUID(), studentId, sectionId, date, status.name());
    }
}
