package com.innovaschools.asistec.infrastructure.adapter.out.persistence.mapper;

import com.innovaschools.asistec.domain.model.Teacher;
import com.innovaschools.asistec.infrastructure.adapter.out.persistence.entity.TeacherEntity;
import org.springframework.stereotype.Component;

@Component
public class TeacherMapper {

    public Teacher toDomain(TeacherEntity entity) {
        return new Teacher(
                entity.getId(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getRole()
        );
    }
}
