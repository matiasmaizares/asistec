package com.innovaschools.asistec.domain.port.out;

import com.innovaschools.asistec.domain.model.AttendanceRecord;
import com.innovaschools.asistec.domain.model.AttendanceStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface AttendanceRecordPort {

    record SectionCounts(long present, long absent, long late) {}

    AttendanceRecord save(AttendanceRecord record);

    void upsert(UUID studentId, UUID sectionId, LocalDate date, AttendanceStatus status);

    Optional<AttendanceRecord> findByStudentAndSectionAndDate(UUID studentId, UUID sectionId, LocalDate date);

    List<AttendanceRecord> findBySectionAndDate(UUID sectionId, LocalDate date);

    Map<UUID, SectionCounts> countBySectionForDate(LocalDate date);

    List<AttendanceRecord> findByStudentAndDateRange(UUID studentId, LocalDate from, LocalDate to);
}
