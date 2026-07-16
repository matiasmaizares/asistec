package com.innovaschools.asistec.domain.service;

import com.innovaschools.asistec.domain.exception.InvalidDateRangeException;
import com.innovaschools.asistec.domain.model.*;
import com.innovaschools.asistec.domain.port.in.GetPendingSectionsUseCase;
import com.innovaschools.asistec.domain.port.out.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceQueryServiceTest {

    @Mock AttendanceRecordPort attendanceRecordPort;
    @Mock SectionPort sectionPort;
    @Mock StudentPort studentPort;

    @InjectMocks AttendanceQueryService service;

    private UUID sectionId;
    private Section section;

    @BeforeEach
    void setUp() {
        sectionId = UUID.randomUUID();
        Grade grade = new Grade(UUID.randomUUID(), "3er Grado");
        section = new Section(sectionId, "A", grade, null);
    }

    @Test
    void getPendingSections_returnsSectionsWithoutAttendanceToday() {
        when(sectionPort.findWithoutAttendanceForDate(LocalDate.now()))
                .thenReturn(List.of(section));

        List<Section> pending = service.getPendingSections(LocalDate.now());

        assertThat(pending).hasSize(1);
        assertThat(pending.get(0).getId()).isEqualTo(sectionId);
    }

    @Test
    void getHistory_invalidDateRange_throwsInvalidDateRangeException() {
        UUID studentId = UUID.randomUUID();
        LocalDate from = LocalDate.now();
        LocalDate to = LocalDate.now().minusDays(1);

        assertThatThrownBy(() -> service.getHistory(studentId, from, to))
                .isInstanceOf(InvalidDateRangeException.class);

        verifyNoInteractions(attendanceRecordPort);
    }

    @Test
    void getHistory_validRange_returnsRecords() {
        UUID studentId = UUID.randomUUID();
        LocalDate from = LocalDate.now().minusDays(5);
        LocalDate to = LocalDate.now();

        AttendanceRecord record = new AttendanceRecord(
                UUID.randomUUID(), studentId, sectionId, from,
                AttendanceStatus.PRESENTE, LocalDateTime.now(), LocalDateTime.now());

        when(attendanceRecordPort.findByStudentAndDateRange(studentId, from, to))
                .thenReturn(List.of(record));

        List<AttendanceRecord> history = service.getHistory(studentId, from, to);

        assertThat(history).hasSize(1);
        assertThat(history.get(0).getStatus()).isEqualTo(AttendanceStatus.PRESENTE);
    }
}
