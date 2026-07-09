package com.innovaschools.asistec.infrastructure.adapter.out.persistence.repository;

import com.innovaschools.asistec.infrastructure.adapter.out.persistence.entity.GradeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GradeJpaRepository extends JpaRepository<GradeEntity, UUID> {
}
