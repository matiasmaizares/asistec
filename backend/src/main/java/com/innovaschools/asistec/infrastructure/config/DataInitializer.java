package com.innovaschools.asistec.infrastructure.config;

import com.innovaschools.asistec.domain.model.AttendanceStatus;
import com.innovaschools.asistec.domain.model.TeacherRole;
import com.innovaschools.asistec.infrastructure.adapter.out.persistence.entity.*;
import com.innovaschools.asistec.infrastructure.adapter.out.persistence.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    private final GradeJpaRepository gradeRepo;
    private final SectionJpaRepository sectionRepo;
    private final StudentJpaRepository studentRepo;
    private final AttendanceRecordJpaRepository attendanceRepo;
    private final TeacherJpaRepository teacherRepo;
    private final PasswordEncoder passwordEncoder;

    private static final AttendanceStatus[] STATUSES = {
        AttendanceStatus.PRESENTE, AttendanceStatus.PRESENTE, AttendanceStatus.PRESENTE,
        AttendanceStatus.AUSENTE, AttendanceStatus.TARDANZA
    };

    public DataInitializer(GradeJpaRepository gradeRepo,
                           SectionJpaRepository sectionRepo,
                           StudentJpaRepository studentRepo,
                           AttendanceRecordJpaRepository attendanceRepo,
                           TeacherJpaRepository teacherRepo,
                           PasswordEncoder passwordEncoder) {
        this.gradeRepo = gradeRepo;
        this.sectionRepo = sectionRepo;
        this.studentRepo = studentRepo;
        this.attendanceRepo = attendanceRepo;
        this.teacherRepo = teacherRepo;
        this.passwordEncoder = passwordEncoder;
    }

    // Fixed UUIDs so the Postman collection works on any fresh database.
    // Sections
    private static final UUID ID_SEC_3A = UUID.fromString("63366fb2-f694-42ce-b6de-b18e3bad7450");
    private static final UUID ID_SEC_3B = UUID.fromString("d29e1f73-a420-4f4b-8a1e-2c2c5bb37842");
    private static final UUID ID_SEC_4A = UUID.fromString("3664b0c7-a873-49ba-a0db-e745a6edf76d");
    private static final UUID ID_SEC_4B = UUID.fromString("3ae74105-3d4d-4142-8989-7431576f9158");
    // Students of 3°A (referenced in Postman error-case requests)
    private static final UUID ID_STU_LUCAS     = UUID.fromString("7fa45ac7-f06b-4dca-9b80-989f5cceaa8a");
    private static final UUID ID_STU_VALENTINA = UUID.fromString("5ada8368-cc3d-49ea-bbe4-167f78b3aad9");
    private static final UUID ID_STU_MATEO     = UUID.fromString("81b76657-74a5-4031-b22c-fa1c48e2745d");
    private static final UUID ID_STU_SOFIA     = UUID.fromString("28616f93-7412-4978-89a2-e9f308709a8c");
    private static final UUID ID_STU_BENJAMIN  = UUID.fromString("66371183-a6aa-4c9e-ba2e-f4fca5973e05");
    private static final UUID ID_STU_ISABELLA  = UUID.fromString("b110243e-5ac7-427f-9923-f363bf8d969e");
    // Teachers (credenciales de demo — password: "docente123"/"coordinador123")
    private static final UUID ID_TEACHER_COORDINADOR = UUID.fromString("1a2b3c4d-0001-4000-8000-000000000001");
    private static final UUID ID_TEACHER_3A = UUID.fromString("1a2b3c4d-0001-4000-8000-000000000002");
    private static final UUID ID_TEACHER_3B = UUID.fromString("1a2b3c4d-0001-4000-8000-000000000003");
    private static final UUID ID_TEACHER_4A = UUID.fromString("1a2b3c4d-0001-4000-8000-000000000004");
    private static final UUID ID_TEACHER_4B = UUID.fromString("1a2b3c4d-0001-4000-8000-000000000005");

    @Override
    @Transactional
    public void run(String... args) {
        if (gradeRepo.count() > 0) return;
        seed();
    }

    private void seed() {
        TeacherEntity coordinador = saveTeacher(ID_TEACHER_COORDINADOR, "Ana Coordinadora",
                "coordinador@asistec.local", "coordinador123", TeacherRole.COORDINADOR);
        TeacherEntity docente3A = saveTeacher(ID_TEACHER_3A, "Profesor 3ero A",
                "docente.3a@asistec.local", "docente123", TeacherRole.DOCENTE);
        TeacherEntity docente3B = saveTeacher(ID_TEACHER_3B, "Profesor 3ero B",
                "docente.3b@asistec.local", "docente123", TeacherRole.DOCENTE);
        TeacherEntity docente4A = saveTeacher(ID_TEACHER_4A, "Profesor 4to A",
                "docente.4a@asistec.local", "docente123", TeacherRole.DOCENTE);
        TeacherEntity docente4B = saveTeacher(ID_TEACHER_4B, "Profesor 4to B",
                "docente.4b@asistec.local", "docente123", TeacherRole.DOCENTE);

        GradeEntity grade3 = gradeRepo.save(new GradeEntity("3er Grado"));
        GradeEntity grade4 = gradeRepo.save(new GradeEntity("4to Grado"));

        SectionEntity sec3A = saveSection(ID_SEC_3A, "A", grade3, docente3A);
        SectionEntity sec3B = saveSection(ID_SEC_3B, "B", grade3, docente3B);
        SectionEntity sec4A = saveSection(ID_SEC_4A, "A", grade4, docente4A);
        SectionEntity sec4B = saveSection(ID_SEC_4B, "B", grade4, docente4B);
        // coordinador (docente.coordinador) no tiene sección asignada: ve/gestiona todas.

        List<StudentEntity> students3A = createStudents(sec3A,
            new Object[]{ID_STU_LUCAS,     "Lucas Romero"},
            new Object[]{ID_STU_VALENTINA, "Valentina Torres"},
            new Object[]{ID_STU_MATEO,     "Mateo García"},
            new Object[]{ID_STU_SOFIA,     "Sofía López"},
            new Object[]{ID_STU_BENJAMIN,  "Benjamín Martínez"},
            new Object[]{ID_STU_ISABELLA,  "Isabella Pérez"});

        List<StudentEntity> students3B = createStudents(sec3B,
            "Santiago Fernández", "Camila González", "Nicolás Rodríguez",
            "Emma Díaz", "Tomás Hernández", "Luciana Morales");

        List<StudentEntity> students4A = createStudents(sec4A,
            "Agustín Ruiz", "Martina Sánchez", "Felipe Jiménez",
            "Catalina Ramírez", "Emilio Vargas", "Renata Castro");

        List<StudentEntity> students4B = createStudents(sec4B,
            "Axel Ortega", "Julieta Gutiérrez", "Rodrigo Reyes",
            "Pilar Herrera", "Ignacio Medina", "Florencia Flores");

        List<LocalDate> last5BusinessDays = getLastNBusinessDays(5);

        // 3°A y 3°B: asistencia de los últimos 5 días hábiles + hoy
        for (LocalDate date : last5BusinessDays) {
            saveAttendanceForSection(students3A, sec3A, date);
            saveAttendanceForSection(students3B, sec3B, date);
        }
        saveAttendanceForSection(students3A, sec3A, LocalDate.now());
        saveAttendanceForSection(students3B, sec3B, LocalDate.now());

        // 4°A y 4°B: asistencia de días anteriores pero NO hoy (quedan pendientes)
        for (LocalDate date : last5BusinessDays) {
            saveAttendanceForSection(students4A, sec4A, date);
        }
    }

    private SectionEntity saveSection(UUID id, String name, GradeEntity grade, TeacherEntity assignedTeacher) {
        SectionEntity section = new SectionEntity(name, grade);
        section.setId(id);
        section.setAssignedTeacher(assignedTeacher);
        return sectionRepo.save(section);
    }

    private TeacherEntity saveTeacher(UUID id, String fullName, String email, String rawPassword, TeacherRole role) {
        TeacherEntity teacher = new TeacherEntity(fullName, email, passwordEncoder.encode(rawPassword), role);
        teacher.setId(id);
        return teacherRepo.save(teacher);
    }

    /** Accepts either String "First Last" or Object[]{ UUID, "First Last" }. */
    private List<StudentEntity> createStudents(SectionEntity section, Object... entries) {
        List<StudentEntity> result = new ArrayList<>();
        for (Object entry : entries) {
            UUID fixedId = null;
            String fullName;
            if (entry instanceof Object[] pair) {
                fixedId  = (UUID) pair[0];
                fullName = (String) pair[1];
            } else {
                fullName = (String) entry;
            }
            String[] parts = fullName.split(" ", 2);
            StudentEntity student = new StudentEntity(parts[0], parts[1], section);
            if (fixedId != null) student.setId(fixedId);
            result.add(studentRepo.save(student));
        }
        return result;
    }

    private void saveAttendanceForSection(List<StudentEntity> students,
                                           SectionEntity section, LocalDate date) {
        Random rnd = new Random();
        for (StudentEntity student : students) {
            AttendanceStatus status = STATUSES[rnd.nextInt(STATUSES.length)];
            attendanceRepo.save(new AttendanceRecordEntity(student, section, date, status));
        }
    }

    private List<LocalDate> getLastNBusinessDays(int n) {
        List<LocalDate> days = new ArrayList<>();
        LocalDate date = LocalDate.now().minusDays(1);
        while (days.size() < n) {
            DayOfWeek dow = date.getDayOfWeek();
            if (dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY) {
                days.add(date);
            }
            date = date.minusDays(1);
        }
        return days;
    }
}
