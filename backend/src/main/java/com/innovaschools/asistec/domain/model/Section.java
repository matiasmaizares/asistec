package com.innovaschools.asistec.domain.model;

import java.util.UUID;

public class Section {
    private final UUID id;
    private final String name;
    private final Grade grade;

    public Section(UUID id, String name, Grade grade) {
        this.id = id;
        this.name = name;
        this.grade = grade;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public Grade getGrade() { return grade; }

    public String getFullName() {
        return grade.getName() + " " + name;
    }
}
