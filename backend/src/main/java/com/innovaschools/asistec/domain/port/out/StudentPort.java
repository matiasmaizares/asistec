package com.innovaschools.asistec.domain.port.out;

import com.innovaschools.asistec.domain.model.Student;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentPort {

    Optional<Student> findById(UUID id);

    List<Student> findBySectionId(UUID sectionId);
}
