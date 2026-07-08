# Attendance Spec — Módulo de Registro de Asistencia

## 1. Flujo completo (Profesor)

1. El profesor abre la app y selecciona su rol (**Profesor**).
2. El sistema muestra las secciones disponibles (ej: 3°A).
3. El profesor selecciona una sección.
4. El sistema consulta si ya existe un registro de asistencia para esa sección en la fecha actual:
   - **Si no existe**: muestra la lista de estudiantes con estado `null` (sin marcar).
   - **Si ya existe**: muestra la lista con los estados guardados (permite corrección).
5. El profesor marca el estado de cada estudiante: `PRESENTE`, `AUSENTE` o `TARDANZA`.
6. El profesor presiona **Guardar**.
7. El frontend envía al backend la lista completa de estados para esa sección y fecha.
8. El backend valida:
   - Que la sección exista.
   - Que la fecha sea el día actual (no se pueden editar días anteriores).
   - Que no exista ya un registro para ese estudiante en esa sección y fecha (upsert si es corrección del mismo día).
9. El backend persiste el registro con timestamp de creación/actualización.
10. El backend devuelve confirmación. El frontend muestra mensaje de éxito.

---

## 2. Contrato del endpoint de guardado

### `POST /api/v1/attendance`

Registra o actualiza la asistencia de una sección para el día actual.

### Request Body

```json
{
  "sectionId": "uuid",
  "date": "2026-07-08",
  "records": [
    { "studentId": "uuid", "status": "PRESENTE" },
    { "studentId": "uuid", "status": "AUSENTE" },
    { "studentId": "uuid", "status": "TARDANZA" }
  ]
}
```

### Posibles respuestas

| HTTP Status | Escenario | Body |
| --- | --- | --- |
| `201 Created` | Registro nuevo guardado exitosamente | `{ "message": "Asistencia registrada correctamente", "savedAt": "2026-07-08T09:15:00Z" }` |
| `200 OK` | Registro del día actual actualizado (corrección) | `{ "message": "Asistencia actualizada correctamente", "savedAt": "2026-07-08T10:30:00Z" }` |
| `400 Bad Request` | Body inválido o campo faltante | `{ "error": "INVALID_REQUEST", "detail": "El campo 'sectionId' es requerido" }` |
| `404 Not Found` | La sección no existe | `{ "error": "SECTION_NOT_FOUND", "detail": "No existe la sección con id: {id}" }` |
| `409 Conflict` | Se intenta guardar para una fecha que no es hoy | `{ "error": "DATE_NOT_EDITABLE", "detail": "Solo se puede registrar asistencia para el día actual" }` |
| `422 Unprocessable Entity` | `studentId` no pertenece a la sección | `{ "error": "STUDENT_NOT_IN_SECTION", "detail": "El estudiante {id} no pertenece a la sección {id}" }` |

> **Decisión de diseño:** El endpoint guarda un batch (un registro por alumno), no un único registro. Por eso el response no incluye un `attendanceId` singular — devolver el ID del primer alumno sería arbitrario e inútil para el cliente. En cambio, se devuelve `message` + `savedAt`, que es lo que el frontend necesita para mostrar feedback al usuario.

---

## 3. Edge cases

### EC-1: Registro duplicado en el mismo día

**Escenario:** El profesor guarda la asistencia, luego intenta guardar de nuevo para la misma sección y fecha.
**Comportamiento esperado:** El sistema realiza un **upsert** — actualiza los registros existentes en lugar de crear duplicados. Responde `200 OK`.
**Garantía en BD:** Constraint único `(student_id, section_id, date)` a nivel de base de datos, independiente de la lógica de aplicación.

### EC-2: Intento de editar un día anterior

**Escenario:** El profesor envía `"date": "2026-07-07"` (ayer).
**Comportamiento esperado:** El backend rechaza la solicitud con `409 Conflict` y el mensaje `DATE_NOT_EDITABLE`. El frontend debe deshabilitar la edición de días pasados antes de permitir el envío.
**Nota:** La fecha de referencia es la del servidor, no la del cliente.

### EC-3: Sección inexistente

**Escenario:** El `sectionId` enviado no existe en la base de datos.
**Comportamiento esperado:** El backend devuelve `404 Not Found` con el error `SECTION_NOT_FOUND`. No se persiste ningún dato.

### EC-4: Lista de estudiantes incompleta

**Escenario:** El profesor envía solo algunos estudiantes (omite marcar a alguno).
**Comportamiento esperado:** El sistema rechaza con `400 Bad Request` indicando que todos los estudiantes de la sección deben tener un estado asignado. No se permite un guardado parcial.

### EC-5: Rango de fechas inválido en reporte (Coordinador)

**Escenario:** El coordinador consulta un historial con `fechaDesde` mayor a `fechaHasta`.
**Comportamiento esperado:** El backend devuelve `400 Bad Request` con el error `INVALID_DATE_RANGE`. No se ejecuta ninguna consulta a la base de datos.
