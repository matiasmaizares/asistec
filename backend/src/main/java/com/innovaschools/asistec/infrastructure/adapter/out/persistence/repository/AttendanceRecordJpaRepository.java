package com.innovaschools.asistec.infrastructure.adapter.out.persistence.repository;

import com.innovaschools.asistec.infrastructure.adapter.out.persistence.entity.AttendanceRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttendanceRecordJpaRepository extends JpaRepository<AttendanceRecordEntity, UUID> {

    Optional<AttendanceRecordEntity> findByStudentIdAndSectionIdAndDate(
            UUID studentId, UUID sectionId, LocalDate date);

    List<AttendanceRecordEntity> findBySectionIdAndDate(UUID sectionId, LocalDate date);

    List<AttendanceRecordEntity> findByStudentIdAndDateBetween(
            UUID studentId, LocalDate from, LocalDate to);

    @Query("""
            SELECT ar.section.id,
                   SUM(CASE WHEN ar.status = com.innovaschools.asistec.domain.model.AttendanceStatus.PRESENTE THEN 1 ELSE 0 END),
                   SUM(CASE WHEN ar.status = com.innovaschools.asistec.domain.model.AttendanceStatus.AUSENTE  THEN 1 ELSE 0 END),
                   SUM(CASE WHEN ar.status = com.innovaschools.asistec.domain.model.AttendanceStatus.TARDANZA THEN 1 ELSE 0 END)
            FROM AttendanceRecordEntity ar
            WHERE ar.date = :date
            GROUP BY ar.section.id
            """)
    List<Object[]> countBySectionAndDate(@Param("date") LocalDate date);

}
