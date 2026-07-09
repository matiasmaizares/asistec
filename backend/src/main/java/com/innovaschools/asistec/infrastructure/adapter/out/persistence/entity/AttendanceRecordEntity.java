package com.innovaschools.asistec.infrastructure.adapter.out.persistence.entity;

import com.innovaschools.asistec.domain.model.AttendanceStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "attendance_records",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_student_section_date",
        columnNames = {"student_id", "section_id", "date"}
    )
)
@Getter @Setter @NoArgsConstructor
public class AttendanceRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentEntity student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private SectionEntity section;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public AttendanceRecordEntity(StudentEntity student, SectionEntity section,
                                   LocalDate date, AttendanceStatus status) {
        this.student = student;
        this.section = section;
        this.date = date;
        this.status = status;
    }
}
