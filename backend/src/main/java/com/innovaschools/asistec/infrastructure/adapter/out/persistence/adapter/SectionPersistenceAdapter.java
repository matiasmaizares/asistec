package com.innovaschools.asistec.infrastructure.adapter.out.persistence.adapter;

import com.innovaschools.asistec.domain.model.Section;
import com.innovaschools.asistec.domain.port.out.SectionPort;
import com.innovaschools.asistec.infrastructure.adapter.out.persistence.mapper.SectionMapper;
import com.innovaschools.asistec.infrastructure.adapter.out.persistence.repository.SectionJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class SectionPersistenceAdapter implements SectionPort {

    private final SectionJpaRepository repository;
    private final SectionMapper mapper;

    public SectionPersistenceAdapter(SectionJpaRepository repository, SectionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Section> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Section> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Section> findWithoutAttendanceForDate(LocalDate date) {
        return repository.findWithoutAttendanceForDate(date).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
