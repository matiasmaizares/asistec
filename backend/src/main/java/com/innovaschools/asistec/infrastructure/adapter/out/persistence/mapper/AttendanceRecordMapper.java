package com.innovaschools.asistec.infrastructure.adapter.out.persistence.mapper;

import com.innovaschools.asistec.domain.model.AttendanceRecord;
import com.innovaschools.asistec.infrastructure.adapter.out.persistence.entity.AttendanceRecordEntity;
import org.springframework.stereotype.Component;

@Component
public class AttendanceRecordMapper {

    public AttendanceRecord toDomain(AttendanceRecordEntity entity) {
        return new AttendanceRecord(
                entity.getId(),
                entity.getStudent().getId(),
                entity.getSection().getId(),
                entity.getDate(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
