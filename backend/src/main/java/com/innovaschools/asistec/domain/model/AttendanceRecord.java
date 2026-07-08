package com.innovaschools.asistec.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class AttendanceRecord {
    private final UUID id;
    private final UUID studentId;
    private final UUID sectionId;
    private final LocalDate date;
    private AttendanceStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AttendanceRecord(UUID id, UUID studentId, UUID sectionId,
                            LocalDate date, AttendanceStatus status,
                            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.studentId = studentId;
        this.sectionId = sectionId;
        this.date = date;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public UUID getStudentId() { return studentId; }
    public UUID getSectionId() { return sectionId; }
    public LocalDate getDate() { return date; }
    public AttendanceStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void updateStatus(AttendanceStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }
}
