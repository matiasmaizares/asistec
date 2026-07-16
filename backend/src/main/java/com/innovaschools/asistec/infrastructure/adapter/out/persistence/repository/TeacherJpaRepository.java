package com.innovaschools.asistec.infrastructure.adapter.out.persistence.repository;

import com.innovaschools.asistec.infrastructure.adapter.out.persistence.entity.TeacherEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TeacherJpaRepository extends JpaRepository<TeacherEntity, UUID> {

    Optional<TeacherEntity> findByEmail(String email);
}
