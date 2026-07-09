package com.innovaschools.asistec.infrastructure.adapter.in.rest;

import com.innovaschools.asistec.domain.model.AttendanceRecord;
import com.innovaschools.asistec.domain.model.Section;
import com.innovaschools.asistec.domain.port.in.GetDailySummaryUseCase;
import com.innovaschools.asistec.domain.port.in.GetPendingSectionsUseCase;
import com.innovaschools.asistec.domain.port.in.GetStudentHistoryUseCase;
import com.innovaschools.asistec.infrastructure.event.SseAttendanceEventAdapter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final GetDailySummaryUseCase getDailySummaryUseCase;
    private final GetStudentHistoryUseCase getStudentHistoryUseCase;
    private final GetPendingSectionsUseCase getPendingSectionsUseCase;
    private final SseAttendanceEventAdapter sseAdapter;

    public ReportController(GetDailySummaryUseCase getDailySummaryUseCase,
                            GetStudentHistoryUseCase getStudentHistoryUseCase,
                            GetPendingSectionsUseCase getPendingSectionsUseCase,
                            SseAttendanceEventAdapter sseAdapter) {
        this.getDailySummaryUseCase = getDailySummaryUseCase;
        this.getStudentHistoryUseCase = getStudentHistoryUseCase;
        this.getPendingSectionsUseCase = getPendingSectionsUseCase;
        this.sseAdapter = sseAdapter;
    }

    @GetMapping("/daily")
    public List<GetDailySummaryUseCase.SectionSummary> getDailySummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return getDailySummaryUseCase.getSummary(date != null ? date : LocalDate.now());
    }

    @GetMapping("/students/{studentId}/history")
    public List<AttendanceHistoryDto> getStudentHistory(
            @PathVariable UUID studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        return getStudentHistoryUseCase.getHistory(studentId, from, to).stream()
                .map(r -> new AttendanceHistoryDto(r.getDate(), r.getStatus(), r.getSectionId()))
                .toList();
    }

    @GetMapping("/sections/pending")
    public List<SectionDto> getPendingSections(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return getPendingSectionsUseCase.getPendingSections(date != null ? date : LocalDate.now())
                .stream()
                .map(s -> new SectionDto(s.getId(), s.getFullName()))
                .toList();
    }

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamAttendanceUpdates() {
        return sseAdapter.subscribe();
    }

    record AttendanceHistoryDto(LocalDate date,
                                com.innovaschools.asistec.domain.model.AttendanceStatus status,
                                UUID sectionId) {}

    record SectionDto(UUID id, String name) {}
}
