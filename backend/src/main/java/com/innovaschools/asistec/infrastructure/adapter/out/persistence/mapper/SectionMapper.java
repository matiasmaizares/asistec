package com.innovaschools.asistec.infrastructure.adapter.out.persistence.mapper;

import com.innovaschools.asistec.domain.model.Grade;
import com.innovaschools.asistec.domain.model.Section;
import com.innovaschools.asistec.infrastructure.adapter.out.persistence.entity.SectionEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SectionMapper {

    public Section toDomain(SectionEntity entity) {
        Grade grade = new Grade(entity.getGrade().getId(), entity.getGrade().getName());
        UUID assignedTeacherId = entity.getAssignedTeacher() != null
                ? entity.getAssignedTeacher().getId()
                : null;
        return new Section(entity.getId(), entity.getName(), grade, assignedTeacherId);
    }
}
