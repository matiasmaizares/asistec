package com.innovaschools.asistec.infrastructure.adapter.out.persistence.repository;

import com.innovaschools.asistec.infrastructure.adapter.out.persistence.entity.AttendanceRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttendanceRecordJpaRepository extends JpaRepository<AttendanceRecordEntity, UUID> {

    Optional<AttendanceRecordEntity> findByStudentIdAndSectionIdAndDate(
            UUID studentId, UUID sectionId, LocalDate date);

    /**
     * Upsert atómico: una sola sentencia, sin ventana entre SELECT e INSERT/UPDATE.
     * Requiere el unique constraint uk_student_section_date (ver changelog de Liquibase).
     */
    @Modifying
    @Query(value = """
            INSERT INTO attendance_records (id, student_id, section_id, date, status, created_at, updated_at)
            VALUES (:id, :studentId, :sectionId, :date, :status, now(), now())
            ON CONFLICT (student_id, section_id, date)
            DO UPDATE SET status = EXCLUDED.status, updated_at = now()
            """, nativeQuery = true)
    void upsertAttendance(@Param("id") UUID id,
                           @Param("studentId") UUID studentId,
                           @Param("sectionId") UUID sectionId,
                           @Param("date") LocalDate date,
                           @Param("status") String status);

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
