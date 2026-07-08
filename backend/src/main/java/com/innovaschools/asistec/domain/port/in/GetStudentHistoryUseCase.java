package com.innovaschools.asistec.domain.port.in;

import com.innovaschools.asistec.domain.model.AttendanceRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface GetStudentHistoryUseCase {

    List<AttendanceRecord> getHistory(UUID studentId, LocalDate from, LocalDate to);
}
