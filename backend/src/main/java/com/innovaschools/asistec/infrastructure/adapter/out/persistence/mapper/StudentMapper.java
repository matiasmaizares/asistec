package com.innovaschools.asistec.infrastructure.adapter.out.persistence.mapper;

import com.innovaschools.asistec.domain.model.Student;
import com.innovaschools.asistec.infrastructure.adapter.out.persistence.entity.StudentEntity;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper {

    public Student toDomain(StudentEntity entity) {
        return new Student(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getSection().getId()
        );
    }
}
