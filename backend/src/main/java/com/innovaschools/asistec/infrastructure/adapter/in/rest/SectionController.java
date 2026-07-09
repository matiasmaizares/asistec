package com.innovaschools.asistec.infrastructure.adapter.in.rest;

import com.innovaschools.asistec.domain.model.Section;
import com.innovaschools.asistec.domain.port.out.SectionPort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sections")
public class SectionController {

    private final SectionPort sectionPort;

    public SectionController(SectionPort sectionPort) {
        this.sectionPort = sectionPort;
    }

    @GetMapping
    public List<SectionResponse> getAll() {
        return sectionPort.findAll().stream()
                .map(s -> new SectionResponse(s.getId(), s.getFullName(), s.getGrade().getName()))
                .toList();
    }

    record SectionResponse(UUID id, String name, String gradeName) {}
}
