package com.innovaschools.asistec.infrastructure.adapter.out.persistence.adapter;

import com.innovaschools.asistec.domain.model.AttendanceRecord;
import com.innovaschools.asistec.domain.model.AttendanceStatus;
import com.innovaschools.asistec.domain.port.out.AttendanceRecordPort;
import com.innovaschools.asistec.infrastructure.adapter.out.persistence.entity.AttendanceRecordEntity;
import com.innovaschools.asistec.infrastructure.adapter.out.persistence.entity.SectionEntity;
import com.innovaschools.asistec.infrastructure.adapter.out.persistence.entity.StudentEntity;
import com.innovaschools.asistec.infrastructure.adapter.out.persistence.mapper.AttendanceRecordMapper;
import com.innovaschools.asistec.infrastructure.adapter.out.persistence.repository.AttendanceRecordJpaRepository;
import com.innovaschools.asistec.infrastructure.adapter.out.persistence.repository.SectionJpaRepository;
import com.innovaschools.asistec.infrastructure.adapter.out.persistence.repository.StudentJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class AttendanceRecordPersistenceAdapter implements AttendanceRecordPort {

    private final AttendanceRecordJpaRepository repository;
    private final StudentJpaRepository studentRepository;
    private final SectionJpaRepository sectionRepository;
    private final AttendanceRecordMapper mapper;
    private final AttendanceUpsertExecutor upsertExecutor;

    public AttendanceRecordPersistenceAdapter(AttendanceRecordJpaRepository repository,
                                               StudentJpaRepository studentRepository,
                                               SectionJpaRepository sectionRepository,
                                               AttendanceRecordMapper mapper,
                                               AttendanceUpsertExecutor upsertExecutor) {
        this.repository = repository;
        this.studentRepository = studentRepository;
        this.sectionRepository = sectionRepository;
        this.mapper = mapper;
        this.upsertExecutor = upsertExecutor;
    }

    @Override
    public void upsert(UUID studentId, UUID sectionId, LocalDate date, AttendanceStatus status) {
        upsertExecutor.upsert(studentId, sectionId, date, status);
    }

    @Override
    public AttendanceRecord save(AttendanceRecord record) {
        AttendanceRecordEntity entity;

        if (record.getId() != null) {
            entity = repository.findById(record.getId())
                    .orElseGet(() -> newEntity(record));
            entity.setStatus(record.getStatus());
        } else {
            entity = newEntity(record);
        }

        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<AttendanceRecord> findByStudentAndSectionAndDate(
            UUID studentId, UUID sectionId, LocalDate date) {
        return repository.findByStudentIdAndSectionIdAndDate(studentId, sectionId, date)
                .map(mapper::toDomain);
    }

    @Override
    public List<AttendanceRecord> findBySectionAndDate(UUID sectionId, LocalDate date) {
        return repository.findBySectionIdAndDate(sectionId, date).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Map<UUID, AttendanceRecordPort.SectionCounts> countBySectionForDate(LocalDate date) {
        return repository.countBySectionAndDate(date).stream()
                .collect(Collectors.toMap(
                        row -> (UUID) row[0],
                        row -> new AttendanceRecordPort.SectionCounts(
                                ((Number) row[1]).longValue(),
                                ((Number) row[2]).longValue(),
                                ((Number) row[3]).longValue()
                        )
                ));
    }

    @Override
    public List<AttendanceRecord> findByStudentAndDateRange(
            UUID studentId, LocalDate from, LocalDate to) {
        return repository.findByStudentIdAndDateBetween(studentId, from, to).stream()
                .map(mapper::toDomain)
                .toList();
    }

    private AttendanceRecordEntity newEntity(AttendanceRecord record) {
        StudentEntity student = studentRepository.getReferenceById(record.getStudentId());
        SectionEntity section = sectionRepository.getReferenceById(record.getSectionId());
        return new AttendanceRecordEntity(student, section, record.getDate(), record.getStatus());
    }
}
