package com.innovaschools.asistec.domain.port.in;

import com.innovaschools.asistec.domain.model.Section;

import java.time.LocalDate;
import java.util.List;

public interface GetPendingSectionsUseCase {

    List<Section> getPendingSections(LocalDate date);
}
