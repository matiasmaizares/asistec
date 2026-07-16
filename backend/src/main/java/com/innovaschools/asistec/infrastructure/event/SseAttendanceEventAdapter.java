package com.innovaschools.asistec.infrastructure.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Mantiene los SseEmitter conectados a ESTA instancia del backend y hace el
 * fan-out local. Quien decide CUÁNDO se dispara ese fan-out ya no es esta
 * clase: puede venir de Redis (RedisAttendanceEventPublisher + AttendanceRedisListener,
 * perfil productivo) o de forma directa (LocalAttendanceEventPublisher, perfil "test").
 */
@Component
public class SseAttendanceEventAdapter {

    public static final String CHANNEL = "attendance-updates";

    private static final Logger log = LoggerFactory.getLogger(SseAttendanceEventAdapter.class);

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);
        emitter.onCompletion(() -> { emitters.remove(emitter); log.info("SSE: emitter completado | activos={}", emitters.size()); });
        emitter.onTimeout(()  -> { emitters.remove(emitter); log.info("SSE: emitter por timeout | activos={}", emitters.size()); });
        emitter.onError(e    -> { emitters.remove(emitter); log.warn("SSE: emitter con error | activos={}", emitters.size()); });
        try {
            emitter.send(SseEmitter.event().name("connected").data("ok"));
            log.info("SSE: coordinador conectado | activos={}", emitters.size());
        } catch (IOException e) {
            emitters.remove(emitter);
        }
        return emitter;
    }

    public void broadcastLocal(UUID sectionId, LocalDate date) {
        List<SseEmitter> dead = new ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("attendance-updated")
                        .data(Map.of("sectionId", sectionId.toString(), "date", date.toString())));
            } catch (IOException e) {
                dead.add(emitter);
            }
        }
        if (!dead.isEmpty()) {
            log.warn("SSE: {} emitter(s) muertos eliminados | sectionId={}", dead.size(), sectionId);
            emitters.removeAll(dead);
        }
        log.info("SSE: evento publicado | sectionId={} date={} receptores={}", sectionId, date, emitters.size() - dead.size());
    }
}
