package com.innovaschools.asistec.domain.model;

import java.util.UUID;

public class Student {
    private final UUID id;
    private final String firstName;
    private final String lastName;
    private final UUID sectionId;

    public Student(UUID id, String firstName, String lastName, UUID sectionId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.sectionId = sectionId;
    }

    public UUID getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public UUID getSectionId() { return sectionId; }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
