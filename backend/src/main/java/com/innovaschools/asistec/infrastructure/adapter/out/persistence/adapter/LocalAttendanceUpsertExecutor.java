package com.innovaschools.asistec.infrastructure.adapter.out.persistence.adapter;

import com.innovaschools.asistec.domain.model.AttendanceStatus;
import com.innovaschools.asistec.infrastructure.adapter.out.persistence.entity.AttendanceRecordEntity;
import com.innovaschools.asistec.infrastructure.adapter.out.persistence.entity.SectionEntity;
import com.innovaschools.asistec.infrastructure.adapter.out.persistence.entity.StudentEntity;
import com.innovaschools.asistec.infrastructure.adapter.out.persistence.repository.AttendanceRecordJpaRepository;
import com.innovaschools.asistec.infrastructure.adapter.out.persistence.repository.SectionJpaRepository;
import com.innovaschools.asistec.infrastructure.adapter.out.persistence.repository.StudentJpaRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
@Profile("test")
class LocalAttendanceUpsertExecutor implements AttendanceUpsertExecutor {

    private final AttendanceRecordJpaRepository repository;
    private final StudentJpaRepository studentRepository;
    private final SectionJpaRepository sectionRepository;

    LocalAttendanceUpsertExecutor(AttendanceRecordJpaRepository repository,
                                   StudentJpaRepository studentRepository,
                                   SectionJpaRepository sectionRepository) {
        this.repository = repository;
        this.studentRepository = studentRepository;
        this.sectionRepository = sectionRepository;
    }

    @Override
    public void upsert(UUID studentId, UUID sectionId, LocalDate date, AttendanceStatus status) {
        repository.findByStudentIdAndSectionIdAndDate(studentId, sectionId, date)
                .ifPresentOrElse(
                        entity -> {
                            entity.setStatus(status);
                            repository.save(entity);
                        },
                        () -> {
                            StudentEntity student = studentRepository.getReferenceById(studentId);
                            SectionEntity section = sectionRepository.getReferenceById(sectionId);
                            repository.save(new AttendanceRecordEntity(student, section, date, status));
                        }
                );
    }
}
