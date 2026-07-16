package com.innovaschools.asistec.infrastructure.config;

import com.innovaschools.asistec.infrastructure.event.AttendanceRedisListener;
import com.innovaschools.asistec.infrastructure.event.SseAttendanceEventAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
@Profile("!test")
public class RedisConfig {

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            AttendanceRedisListener listener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listener, new ChannelTopic(SseAttendanceEventAdapter.CHANNEL));
        return container;
    }
}
