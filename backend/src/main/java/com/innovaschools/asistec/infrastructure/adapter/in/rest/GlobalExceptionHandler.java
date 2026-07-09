package com.innovaschools.asistec.infrastructure.adapter.in.rest;

import com.innovaschools.asistec.domain.exception.DateNotEditableException;
import com.innovaschools.asistec.domain.exception.IncompleteAttendanceException;
import com.innovaschools.asistec.domain.exception.InvalidDateRangeException;
import com.innovaschools.asistec.domain.exception.SectionNotFoundException;
import com.innovaschools.asistec.domain.exception.StudentNotInSectionException;
import com.innovaschools.asistec.infrastructure.adapter.in.rest.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(SectionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleSectionNotFound(SectionNotFoundException ex) {
        return ErrorResponse.of("SECTION_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(DateNotEditableException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDateNotEditable(DateNotEditableException ex) {
        log.warn("Intento de editar fecha pasada: {}", ex.getMessage());
        return ErrorResponse.of("DATE_NOT_EDITABLE", ex.getMessage());
    }

    @ExceptionHandler(StudentNotInSectionException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponse handleStudentNotInSection(StudentNotInSectionException ex) {
        return ErrorResponse.of("STUDENT_NOT_IN_SECTION", ex.getMessage());
    }

    @ExceptionHandler(InvalidDateRangeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidDateRange(InvalidDateRangeException ex) {
        return ErrorResponse.of("INVALID_DATE_RANGE", ex.getMessage());
    }

    @ExceptionHandler(IncompleteAttendanceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncompleteAttendance(IncompleteAttendanceException ex) {
        log.warn("Lista de asistencia incompleta: {}", ex.getMessage());
        return ErrorResponse.of("INCOMPLETE_ATTENDANCE", ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrity(DataIntegrityViolationException ex) {
        log.error("Violación de constraint en BD — posible race condition: {}", ex.getMessage());
        return ErrorResponse.of("DUPLICATE_ATTENDANCE", "Ya existe un registro de asistencia para ese alumno en esta sección y fecha");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Datos de entrada inválidos");
        return ErrorResponse.of("INVALID_REQUEST", detail);
    }
}
