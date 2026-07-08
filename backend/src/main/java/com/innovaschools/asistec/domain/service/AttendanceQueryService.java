package com.innovaschools.asistec.domain.service;

import com.innovaschools.asistec.domain.exception.InvalidDateRangeException;
import com.innovaschools.asistec.domain.exception.SectionNotFoundException;
import com.innovaschools.asistec.domain.model.AttendanceRecord;
import com.innovaschools.asistec.domain.model.AttendanceStatus;
import com.innovaschools.asistec.domain.model.Section;
import com.innovaschools.asistec.domain.model.Student;
import com.innovaschools.asistec.domain.port.in.GetDailySummaryUseCase;
import com.innovaschools.asistec.domain.port.in.GetPendingSectionsUseCase;
import com.innovaschools.asistec.domain.port.in.GetSectionAttendanceUseCase;
import com.innovaschools.asistec.domain.port.in.GetStudentHistoryUseCase;
import com.innovaschools.asistec.domain.port.out.AttendanceRecordPort;
import com.innovaschools.asistec.domain.port.out.SectionPort;
import com.innovaschools.asistec.domain.port.out.StudentPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AttendanceQueryService implements
        GetSectionAttendanceUseCase,
        GetDailySummaryUseCase,
        GetStudentHistoryUseCase,
        GetPendingSectionsUseCase {

    private final AttendanceRecordPort attendanceRecordPort;
    private final SectionPort sectionPort;
    private final StudentPort studentPort;

    public AttendanceQueryService(AttendanceRecordPort attendanceRecordPort,
                                  SectionPort sectionPort,
                                  StudentPort studentPort) {
        this.attendanceRecordPort = attendanceRecordPort;
        this.sectionPort = sectionPort;
        this.studentPort = studentPort;
    }

    @Override
    public List<StudentAttendance> getForSection(UUID sectionId, LocalDate date) {
        sectionPort.findById(sectionId)
                .orElseThrow(() -> new SectionNotFoundException(sectionId));

        List<Student> students = studentPort.findBySectionId(sectionId);
        Map<UUID, AttendanceRecord> recordsByStudent = attendanceRecordPort
                .findBySectionAndDate(sectionId, date)
                .stream()
                .collect(Collectors.toMap(AttendanceRecord::getStudentId, r -> r));

        return students.stream()
                .map(s -> {
                    AttendanceRecord record = recordsByStudent.get(s.getId());
                    AttendanceStatus status = record != null ? record.getStatus() : null;
                    return new StudentAttendance(s.getId(), s.getFullName(), status);
                })
                .toList();
    }

    @Override
    public List<SectionSummary> getSummary(LocalDate date) {
        List<Section> allSections = sectionPort.findAll();
        Map<UUID, AttendanceRecordPort.SectionCounts> counts = attendanceRecordPort.countBySectionForDate(date);

        return allSections.stream()
                .map(section -> {
                    AttendanceRecordPort.SectionCounts c = counts.getOrDefault(
                            section.getId(), new AttendanceRecordPort.SectionCounts(0, 0, 0));
                    return new SectionSummary(section.getId(), section.getFullName(),
                            c.present(), c.absent(), c.late(), counts.containsKey(section.getId()));
                })
                .toList();
    }

    @Override
    public List<AttendanceRecord> getHistory(UUID studentId, LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new InvalidDateRangeException();
        }
        return attendanceRecordPort.findByStudentAndDateRange(studentId, from, to);
    }

    @Override
    public List<Section> getPendingSections(LocalDate date) {
        return sectionPort.findWithoutAttendanceForDate(date);
    }

}
