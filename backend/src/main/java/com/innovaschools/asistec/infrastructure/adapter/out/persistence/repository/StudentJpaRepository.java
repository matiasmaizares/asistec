package com.innovaschools.asistec.infrastructure.adapter.out.persistence.repository;

import com.innovaschools.asistec.infrastructure.adapter.out.persistence.entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StudentJpaRepository extends JpaRepository<StudentEntity, UUID> {

    List<StudentEntity> findBySectionId(UUID sectionId);
}
