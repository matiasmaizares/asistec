package com.innovaschools.asistec.domain.port.in;

import com.innovaschools.asistec.domain.model.AttendanceStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface GetSectionAttendanceUseCase {

    List<StudentAttendance> getForSection(UUID sectionId, LocalDate date);

    record StudentAttendance(UUID studentId, String fullName, AttendanceStatus status) {}
}
