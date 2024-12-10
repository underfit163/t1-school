package ru.t1.t1school.service;

import ru.t1.t1school.dto.NotificationTaskDto;

public interface KafkaProducerService {
    /**
     * Отправка уведомления об изменении статуса задачи
     * @param notificationTaskDto задача о которой уведомляют
     */
    void sendStatusTaskUpdate(NotificationTaskDto notificationTaskDto);
}
