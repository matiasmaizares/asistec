package com.innovaschools.asistec.infrastructure.event;

import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
@Profile("!test")
public class AttendanceRedisListener implements MessageListener {

    private final SseAttendanceEventAdapter sseAdapter;

    public AttendanceRedisListener(SseAttendanceEventAdapter sseAdapter) {
        this.sseAdapter = sseAdapter;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String[] parts = new String(message.getBody()).split("\\|", 2);
        sseAdapter.broadcastLocal(UUID.fromString(parts[0]), LocalDate.parse(parts[1]));
    }
}
