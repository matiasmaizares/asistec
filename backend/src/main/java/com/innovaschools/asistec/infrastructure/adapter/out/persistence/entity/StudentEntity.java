package com.innovaschools.asistec.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "students")
@Getter @Setter @NoArgsConstructor
public class StudentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private SectionEntity section;

    public StudentEntity(String firstName, String lastName, SectionEntity section) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.section = section;
    }
}
