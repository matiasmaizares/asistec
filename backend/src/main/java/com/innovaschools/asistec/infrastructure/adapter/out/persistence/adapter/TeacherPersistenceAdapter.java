package com.innovaschools.asistec.infrastructure.adapter.out.persistence.adapter;

import com.innovaschools.asistec.domain.model.Teacher;
import com.innovaschools.asistec.domain.port.out.TeacherPort;
import com.innovaschools.asistec.infrastructure.adapter.out.persistence.mapper.TeacherMapper;
import com.innovaschools.asistec.infrastructure.adapter.out.persistence.repository.TeacherJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class TeacherPersistenceAdapter implements TeacherPort {

    private final TeacherJpaRepository repository;
    private final TeacherMapper mapper;

    public TeacherPersistenceAdapter(TeacherJpaRepository repository, TeacherMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Teacher> findByEmail(String email) {
        return repository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public Optional<Teacher> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }
}
