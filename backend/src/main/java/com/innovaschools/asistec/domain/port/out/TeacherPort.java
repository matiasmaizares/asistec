package com.innovaschools.asistec.domain.port.out;

import com.innovaschools.asistec.domain.model.Teacher;

import java.util.Optional;
import java.util.UUID;

public interface TeacherPort {

    Optional<Teacher> findByEmail(String email);

    Optional<Teacher> findById(UUID id);
}
