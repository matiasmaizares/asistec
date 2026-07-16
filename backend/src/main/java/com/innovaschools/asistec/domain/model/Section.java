package com.innovaschools.asistec.domain.model;

import java.util.UUID;

public class Section {
    private final UUID id;
    private final String name;
    private final Grade grade;
    private final UUID assignedTeacherId;

    public Section(UUID id, String name, Grade grade, UUID assignedTeacherId) {
        this.id = id;
        this.name = name;
        this.grade = grade;
        this.assignedTeacherId = assignedTeacherId;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public Grade getGrade() { return grade; }
    public UUID getAssignedTeacherId() { return assignedTeacherId; }

    public String getFullName() {
        return grade.getName() + " " + name;
    }
}
