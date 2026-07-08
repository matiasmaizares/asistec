package com.innovaschools.asistec.domain.port.out;

import java.time.LocalDate;
import java.util.UUID;

public interface AttendanceEventPort {

    void publishAttendanceUpdated(UUID sectionId, LocalDate date);
}
