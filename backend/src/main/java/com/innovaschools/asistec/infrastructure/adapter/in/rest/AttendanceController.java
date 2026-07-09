package com.innovaschools.asistec.infrastructure.adapter.in.rest;

import com.innovaschools.asistec.domain.model.AttendanceStatus;
import com.innovaschools.asistec.domain.port.in.GetSectionAttendanceUseCase;
import com.innovaschools.asistec.domain.port.in.RegisterAttendanceUseCase;
import com.innovaschools.asistec.infrastructure.adapter.in.rest.dto.AttendanceResponse;
import com.innovaschools.asistec.infrastructure.adapter.in.rest.dto.RegisterAttendanceRequest;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/attendance")
public class AttendanceController {

    private final RegisterAttendanceUseCase registerAttendanceUseCase;
    private final GetSectionAttendanceUseCase getSectionAttendanceUseCase;

    public AttendanceController(RegisterAttendanceUseCase registerAttendanceUseCase,
                                GetSectionAttendanceUseCase getSectionAttendanceUseCase) {
        this.registerAttendanceUseCase = registerAttendanceUseCase;
        this.getSectionAttendanceUseCase = getSectionAttendanceUseCase;
    }

    @PostMapping
    public ResponseEntity<AttendanceResponse> register(
            @Valid @RequestBody RegisterAttendanceRequest request) {

        List<RegisterAttendanceUseCase.Command.StudentEntry> entries = request.records().stream()
                .map(r -> new RegisterAttendanceUseCase.Command.StudentEntry(r.studentId(), r.status()))
                .toList();

        RegisterAttendanceUseCase.Command command = new RegisterAttendanceUseCase.Command(
                request.sectionId(), request.date(), entries);

        RegisterAttendanceUseCase.Result result = registerAttendanceUseCase.register(command);

        if (result.isNew()) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(AttendanceResponse.created(result.savedAt()));
        }
        return ResponseEntity.ok(AttendanceResponse.updated(result.savedAt()));
    }

    @GetMapping("/{sectionId}")
    public List<StudentAttendanceDto> getForSection(
            @PathVariable UUID sectionId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDate queryDate = date != null ? date : LocalDate.now();

        return getSectionAttendanceUseCase.getForSection(sectionId, queryDate).stream()
                .map(sa -> new StudentAttendanceDto(sa.studentId(), sa.fullName(), sa.status()))
                .toList();
    }

    record StudentAttendanceDto(UUID studentId, String fullName, AttendanceStatus status) {}
}
