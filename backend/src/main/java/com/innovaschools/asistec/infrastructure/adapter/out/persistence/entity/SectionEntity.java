package com.innovaschools.asistec.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "sections")
@Getter @Setter @NoArgsConstructor
public class SectionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id", nullable = false)
    private GradeEntity grade;

    public SectionEntity(String name, GradeEntity grade) {
        this.name = name;
        this.grade = grade;
    }
}
