package com.innovaschools.asistec.infrastructure.adapter.out.persistence.adapter;

import com.innovaschools.asistec.domain.model.Student;
import com.innovaschools.asistec.domain.port.out.StudentPort;
import com.innovaschools.asistec.infrastructure.adapter.out.persistence.mapper.StudentMapper;
import com.innovaschools.asistec.infrastructure.adapter.out.persistence.repository.StudentJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class StudentPersistenceAdapter implements StudentPort {

    private final StudentJpaRepository repository;
    private final StudentMapper mapper;

    public StudentPersistenceAdapter(StudentJpaRepository repository, StudentMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Student> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Student> findBySectionId(UUID sectionId) {
        return repository.findBySectionId(sectionId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
