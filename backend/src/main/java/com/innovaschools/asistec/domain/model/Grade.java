package com.innovaschools.asistec.domain.model;

import java.util.UUID;

public class Grade {
    private final UUID id;
    private final String name;

    public Grade(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
}
