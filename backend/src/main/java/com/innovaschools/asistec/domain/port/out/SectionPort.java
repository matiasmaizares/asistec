package com.innovaschools.asistec.domain.port.out;

import com.innovaschools.asistec.domain.model.Section;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SectionPort {

    Optional<Section> findById(UUID id);

    List<Section> findAll();

    List<Section> findWithoutAttendanceForDate(LocalDate date);
}
