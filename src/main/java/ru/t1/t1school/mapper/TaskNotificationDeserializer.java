package ru.t1.t1school.mapper;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskNotificationDeserializer<T> extends JsonDeserializer<T> {

    private static String getMessage(byte[] data) {
        return new String(data, StandardCharsets.UTF_8);
    }

    @Override
    public T deserialize(String topic, Headers headers, byte[] data) {
        try {
            return super.deserialize(topic, headers, data);
        } catch (Exception e) {
            log.error("Произошла ошибка во время десериализации сообщения. Тема: '{}', Данные: '{}'",
                    topic,
                    getMessage(data));
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        try {
            return super.deserialize(topic, data);
        } catch (Exception e) {
            log.error("Произошла ошибка во время десериализации сообщения. Тема: '{}', Данные: '{}'",
                    topic,
                    getMessage(data));
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }
}
