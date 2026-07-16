package com.innovaschools.asistec.infrastructure.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovaschools.asistec.domain.model.AttendanceStatus;
import com.innovaschools.asistec.infrastructure.adapter.in.rest.dto.RegisterAttendanceRequest;
import com.innovaschools.asistec.infrastructure.adapter.out.persistence.entity.*;
import com.innovaschools.asistec.infrastructure.adapter.out.persistence.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AttendanceControllerIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired GradeJpaRepository gradeRepo;
    @Autowired SectionJpaRepository sectionRepo;
    @Autowired StudentJpaRepository studentRepo;
    @Autowired AttendanceRecordJpaRepository attendanceRepo;

    private SectionEntity section;
    private StudentEntity student;
    private String authHeader;

    @BeforeEach
    void setUp() throws Exception {
        attendanceRepo.deleteAll();
        studentRepo.deleteAll();
        sectionRepo.deleteAll();
        gradeRepo.deleteAll();

        GradeEntity grade = gradeRepo.save(new GradeEntity("3er Grado"));
        section = sectionRepo.save(new SectionEntity("A", grade));
        student = studentRepo.save(new StudentEntity("Lucas", "Romero", section));

        // Coordinador: no depende de que la sección de prueba tenga un docente
        // asignado (a diferencia de un DOCENTE, que solo puede tocar su sección).
        String body = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"coordinador@asistec.local","password":"coordinador123"}"""))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String accessToken = objectMapper.readTree(body).get("accessToken").asText();
        authHeader = "Bearer " + accessToken;
    }

    @Test
    void postAttendance_newRecord_returns201() throws Exception {
        RegisterAttendanceRequest request = new RegisterAttendanceRequest(
                section.getId(),
                LocalDate.now(),
                List.of(new RegisterAttendanceRequest.StudentStatusRequest(
                        student.getId(), AttendanceStatus.PRESENTE))
        );

        mockMvc.perform(post("/api/v1/attendance")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message", containsString("registrada")));
    }

    @Test
    void postAttendance_duplicateSameDay_returns200WithUpdate() throws Exception {
        RegisterAttendanceRequest request = new RegisterAttendanceRequest(
                section.getId(),
                LocalDate.now(),
                List.of(new RegisterAttendanceRequest.StudentStatusRequest(
                        student.getId(), AttendanceStatus.AUSENTE))
        );

        // First save
        mockMvc.perform(post("/api/v1/attendance")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Second save (correction)
        RegisterAttendanceRequest correction = new RegisterAttendanceRequest(
                section.getId(),
                LocalDate.now(),
                List.of(new RegisterAttendanceRequest.StudentStatusRequest(
                        student.getId(), AttendanceStatus.TARDANZA))
        );

        mockMvc.perform(post("/api/v1/attendance")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correction)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("actualizada")));
    }

    @Test
    void postAttendance_pastDate_returns409() throws Exception {
        RegisterAttendanceRequest request = new RegisterAttendanceRequest(
                section.getId(),
                LocalDate.now().minusDays(1),
                List.of(new RegisterAttendanceRequest.StudentStatusRequest(
                        student.getId(), AttendanceStatus.PRESENTE))
        );

        mockMvc.perform(post("/api/v1/attendance")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", is("DATE_NOT_EDITABLE")));
    }

    @Test
    void postAttendance_nonExistentSection_returns404() throws Exception {
        RegisterAttendanceRequest request = new RegisterAttendanceRequest(
                java.util.UUID.randomUUID(),
                LocalDate.now(),
                List.of(new RegisterAttendanceRequest.StudentStatusRequest(
                        student.getId(), AttendanceStatus.PRESENTE))
        );

        mockMvc.perform(post("/api/v1/attendance")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("SECTION_NOT_FOUND")));
    }

    @Test
    void getSectionsWithoutAttendanceToday_returnsPendingSections() throws Exception {
        mockMvc.perform(get("/api/v1/reports/sections/pending")
                        .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }
}
