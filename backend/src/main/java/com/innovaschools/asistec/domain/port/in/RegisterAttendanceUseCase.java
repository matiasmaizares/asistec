package com.innovaschools.asistec.domain.port.in;

import com.innovaschools.asistec.domain.model.AttendanceStatus;
import com.innovaschools.asistec.domain.model.TeacherRole;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface RegisterAttendanceUseCase {

    Result register(Command command);

    record Command(UUID sectionId, LocalDate date, List<StudentEntry> records,
                    UUID callerId, TeacherRole callerRole) {
        public record StudentEntry(UUID studentId, AttendanceStatus status) {}
    }

    record Result(boolean isNew, LocalDateTime savedAt) {}
}
