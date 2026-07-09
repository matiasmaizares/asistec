package com.innovaschools.asistec.infrastructure.adapter.in.rest.dto;

import java.time.LocalDateTime;

public record ErrorResponse(String error, String detail, LocalDateTime timestamp) {

    public static ErrorResponse of(String error, String detail) {
        return new ErrorResponse(error, detail, LocalDateTime.now());
    }
}
