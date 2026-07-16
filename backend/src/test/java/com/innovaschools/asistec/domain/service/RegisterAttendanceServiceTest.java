package com.innovaschools.asistec.domain.service;

import com.innovaschools.asistec.domain.exception.DateNotEditableException;
import com.innovaschools.asistec.domain.exception.IncompleteAttendanceException;
import com.innovaschools.asistec.domain.exception.NotAssignedToSectionException;
import com.innovaschools.asistec.domain.exception.SectionNotFoundException;
import com.innovaschools.asistec.domain.exception.StudentNotInSectionException;
import com.innovaschools.asistec.domain.model.*;
import com.innovaschools.asistec.domain.port.in.RegisterAttendanceUseCase;
import com.innovaschools.asistec.domain.port.out.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterAttendanceServiceTest {

    @Mock AttendanceRecordPort attendanceRecordPort;
    @Mock SectionPort sectionPort;
    @Mock StudentPort studentPort;
    @Mock AttendanceEventPort eventPort;

    @InjectMocks RegisterAttendanceService service;

    private UUID sectionId;
    private UUID studentId;
    private UUID coordinatorId;
    private Section section;
    private Student student;

    @BeforeEach
    void setUp() {
        sectionId = UUID.randomUUID();
        studentId = UUID.randomUUID();
        coordinatorId = UUID.randomUUID();
        Grade grade = new Grade(UUID.randomUUID(), "3er Grado");
        section = new Section(sectionId, "A", grade, null);
        student = new Student(studentId, "Lucas", "Romero", sectionId);
    }

    @Test
    void register_newAttendance_returnsIsNewTrue() {
        when(sectionPort.findById(sectionId)).thenReturn(Optional.of(section));
        when(studentPort.findBySectionId(sectionId)).thenReturn(List.of(student));
        when(attendanceRecordPort.findBySectionAndDate(sectionId, LocalDate.now()))
                .thenReturn(List.of());

        RegisterAttendanceUseCase.Command command = new RegisterAttendanceUseCase.Command(
                sectionId,
                LocalDate.now(),
                List.of(new RegisterAttendanceUseCase.Command.StudentEntry(studentId, AttendanceStatus.PRESENTE)),
                coordinatorId, TeacherRole.COORDINADOR
        );

        RegisterAttendanceUseCase.Result result = service.register(command);

        assertThat(result.isNew()).isTrue();
        verify(attendanceRecordPort).upsert(studentId, sectionId, LocalDate.now(), AttendanceStatus.PRESENTE);
        verify(eventPort).publishAttendanceUpdated(sectionId, LocalDate.now());
    }

    @Test
    void register_existingAttendanceSameDay_updatesAndReturnsIsNewFalse() {
        AttendanceRecord existing = new AttendanceRecord(
                UUID.randomUUID(), studentId, sectionId, LocalDate.now(),
                AttendanceStatus.AUSENTE, null, null);

        when(sectionPort.findById(sectionId)).thenReturn(Optional.of(section));
        when(studentPort.findBySectionId(sectionId)).thenReturn(List.of(student));
        when(attendanceRecordPort.findBySectionAndDate(sectionId, LocalDate.now()))
                .thenReturn(List.of(existing));

        RegisterAttendanceUseCase.Command command = new RegisterAttendanceUseCase.Command(
                sectionId,
                LocalDate.now(),
                List.of(new RegisterAttendanceUseCase.Command.StudentEntry(studentId, AttendanceStatus.PRESENTE)),
                coordinatorId, TeacherRole.COORDINADOR
        );

        RegisterAttendanceUseCase.Result result = service.register(command);

        assertThat(result.isNew()).isFalse();
        verify(attendanceRecordPort).upsert(studentId, sectionId, LocalDate.now(), AttendanceStatus.PRESENTE);
    }

    @Test
    void register_pastDate_throwsDateNotEditableException() {
        RegisterAttendanceUseCase.Command command = new RegisterAttendanceUseCase.Command(
                sectionId,
                LocalDate.now().minusDays(1),
                List.of(new RegisterAttendanceUseCase.Command.StudentEntry(studentId, AttendanceStatus.PRESENTE)),
                coordinatorId, TeacherRole.COORDINADOR
        );

        assertThatThrownBy(() -> service.register(command))
                .isInstanceOf(DateNotEditableException.class);

        verifyNoInteractions(attendanceRecordPort, eventPort);
    }

    @Test
    void register_nonExistentSection_throwsSectionNotFoundException() {
        when(sectionPort.findById(sectionId)).thenReturn(Optional.empty());

        RegisterAttendanceUseCase.Command command = new RegisterAttendanceUseCase.Command(
                sectionId,
                LocalDate.now(),
                List.of(new RegisterAttendanceUseCase.Command.StudentEntry(studentId, AttendanceStatus.PRESENTE)),
                coordinatorId, TeacherRole.COORDINADOR
        );

        assertThatThrownBy(() -> service.register(command))
                .isInstanceOf(SectionNotFoundException.class);
    }

    @Test
    void register_incompleteStudentList_throwsIncompleteAttendanceException() {
        Student student2 = new Student(UUID.randomUUID(), "Valentina", "Torres", sectionId);
        when(sectionPort.findById(sectionId)).thenReturn(Optional.of(section));
        when(studentPort.findBySectionId(sectionId)).thenReturn(List.of(student, student2));

        RegisterAttendanceUseCase.Command command = new RegisterAttendanceUseCase.Command(
                sectionId,
                LocalDate.now(),
                List.of(new RegisterAttendanceUseCase.Command.StudentEntry(studentId, AttendanceStatus.PRESENTE)),
                coordinatorId, TeacherRole.COORDINADOR
        );

        assertThatThrownBy(() -> service.register(command))
                .isInstanceOf(IncompleteAttendanceException.class);
    }

    @Test
    void register_studentNotInSection_throwsStudentNotInSectionException() {
        UUID otherStudentId = UUID.randomUUID();
        when(sectionPort.findById(sectionId)).thenReturn(Optional.of(section));
        when(studentPort.findBySectionId(sectionId)).thenReturn(List.of(student));

        RegisterAttendanceUseCase.Command command = new RegisterAttendanceUseCase.Command(
                sectionId,
                LocalDate.now(),
                List.of(new RegisterAttendanceUseCase.Command.StudentEntry(otherStudentId, AttendanceStatus.PRESENTE)),
                coordinatorId, TeacherRole.COORDINADOR
        );

        assertThatThrownBy(() -> service.register(command))
                .isInstanceOf(StudentNotInSectionException.class);
    }

    @Test
    void register_docenteNotAssignedToSection_throwsNotAssignedToSectionException() {
        UUID otherTeacherId = UUID.randomUUID();
        when(sectionPort.findById(sectionId)).thenReturn(Optional.of(section));

        RegisterAttendanceUseCase.Command command = new RegisterAttendanceUseCase.Command(
                sectionId,
                LocalDate.now(),
                List.of(new RegisterAttendanceUseCase.Command.StudentEntry(studentId, AttendanceStatus.PRESENTE)),
                otherTeacherId, TeacherRole.DOCENTE
        );

        assertThatThrownBy(() -> service.register(command))
                .isInstanceOf(NotAssignedToSectionException.class);

        verifyNoInteractions(attendanceRecordPort, eventPort);
    }

    @Test
    void register_docenteAssignedToSection_succeeds() {
        UUID assignedTeacherId = UUID.randomUUID();
        Grade grade = new Grade(UUID.randomUUID(), "3er Grado");
        Section assignedSection = new Section(sectionId, "A", grade, assignedTeacherId);

        when(sectionPort.findById(sectionId)).thenReturn(Optional.of(assignedSection));
        when(studentPort.findBySectionId(sectionId)).thenReturn(List.of(student));
        when(attendanceRecordPort.findBySectionAndDate(sectionId, LocalDate.now()))
                .thenReturn(List.of());

        RegisterAttendanceUseCase.Command command = new RegisterAttendanceUseCase.Command(
                sectionId,
                LocalDate.now(),
                List.of(new RegisterAttendanceUseCase.Command.StudentEntry(studentId, AttendanceStatus.PRESENTE)),
                assignedTeacherId, TeacherRole.DOCENTE
        );

        RegisterAttendanceUseCase.Result result = service.register(command);

        assertThat(result.isNew()).isTrue();
        verify(attendanceRecordPort).upsert(studentId, sectionId, LocalDate.now(), AttendanceStatus.PRESENTE);
    }
}
