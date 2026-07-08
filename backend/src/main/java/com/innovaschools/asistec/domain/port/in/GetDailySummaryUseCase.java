package com.innovaschools.asistec.domain.port.in;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface GetDailySummaryUseCase {

    List<SectionSummary> getSummary(LocalDate date);

    record SectionSummary(
            UUID sectionId,
            String sectionName,
            long presentCount,
            long absentCount,
            long lateCount,
            boolean hasAttendance
    ) {}
}
