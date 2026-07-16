package com.innovaschools.asistec.domain.model;

import java.util.UUID;

public class Teacher {
    private final UUID id;
    private final String fullName;
    private final String email;
    private final String passwordHash;
    private final TeacherRole role;

    public Teacher(UUID id, String fullName, String email, String passwordHash, TeacherRole role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public UUID getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public TeacherRole getRole() { return role; }
}
