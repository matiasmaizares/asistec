package com.innovaschools.asistec.domain.service;

import com.innovaschools.asistec.domain.exception.DateNotEditableException;
import com.innovaschools.asistec.domain.exception.IncompleteAttendanceException;
import com.innovaschools.asistec.domain.exception.NotAssignedToSectionException;
import com.innovaschools.asistec.domain.exception.SectionNotFoundException;
import com.innovaschools.asistec.domain.exception.StudentNotInSectionException;
import com.innovaschools.asistec.domain.model.Section;
import com.innovaschools.asistec.domain.model.Student;
import com.innovaschools.asistec.domain.model.TeacherRole;
import com.innovaschools.asistec.domain.port.in.RegisterAttendanceUseCase;
import com.innovaschools.asistec.domain.port.out.AttendanceEventPort;
import com.innovaschools.asistec.domain.port.out.AttendanceRecordPort;
import com.innovaschools.asistec.domain.port.out.SectionPort;
import com.innovaschools.asistec.domain.port.out.StudentPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RegisterAttendanceService implements RegisterAttendanceUseCase {

    private static final Logger log = LoggerFactory.getLogger(RegisterAttendanceService.class);

    private final AttendanceRecordPort attendanceRecordPort;
    private final SectionPort sectionPort;
    private final StudentPort studentPort;
    private final AttendanceEventPort eventPort;

    public RegisterAttendanceService(AttendanceRecordPort attendanceRecordPort,
                                     SectionPort sectionPort,
                                     StudentPort studentPort,
                                     AttendanceEventPort eventPort) {
        this.attendanceRecordPort = attendanceRecordPort;
        this.sectionPort = sectionPort;
        this.studentPort = studentPort;
        this.eventPort = eventPort;
    }

    @Override
    @Transactional
    public Result register(Command command) {
        if (!command.date().equals(LocalDate.now())) {
            throw new DateNotEditableException();
        }

        Section section = sectionPort.findById(command.sectionId())
                .orElseThrow(() -> new SectionNotFoundException(command.sectionId()));

        if (command.callerRole() == TeacherRole.DOCENTE
                && !command.callerId().equals(section.getAssignedTeacherId())) {
            throw new NotAssignedToSectionException(command.sectionId());
        }

        Set<UUID> sectionStudentIds = studentPort.findBySectionId(command.sectionId())
                .stream()
                .map(Student::getId)
                .collect(Collectors.toSet());

        if (command.records().size() != sectionStudentIds.size()) {
            throw new IncompleteAttendanceException(sectionStudentIds.size(), command.records().size());
        }

        for (Command.StudentEntry entry : command.records()) {
            if (!sectionStudentIds.contains(entry.studentId())) {
                throw new StudentNotInSectionException(entry.studentId(), command.sectionId());
            }
        }

        boolean isNew = attendanceRecordPort.findBySectionAndDate(command.sectionId(), command.date()).isEmpty();

        for (Command.StudentEntry entry : command.records()) {
            attendanceRecordPort.upsert(entry.studentId(), command.sectionId(), command.date(), entry.status());
        }

        eventPort.publishAttendanceUpdated(command.sectionId(), command.date());

        log.info("Asistencia {} | sectionId={} date={} alumnos={}",
                isNew ? "registrada" : "actualizada",
                command.sectionId(), command.date(), command.records().size());

        return new Result(isNew, LocalDateTime.now());
    }
}
