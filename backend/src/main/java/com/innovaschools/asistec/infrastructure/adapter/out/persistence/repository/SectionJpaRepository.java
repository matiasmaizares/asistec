package com.innovaschools.asistec.infrastructure.adapter.out.persistence.repository;

import com.innovaschools.asistec.infrastructure.adapter.out.persistence.entity.SectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface SectionJpaRepository extends JpaRepository<SectionEntity, UUID> {

    @Query("SELECT s FROM SectionEntity s WHERE s.id NOT IN " +
           "(SELECT DISTINCT a.section.id FROM AttendanceRecordEntity a WHERE a.date = :date)")
    List<SectionEntity> findWithoutAttendanceForDate(@Param("date") LocalDate date);
}
