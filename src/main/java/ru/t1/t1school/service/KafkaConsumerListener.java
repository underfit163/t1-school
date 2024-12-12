package ru.t1.t1school.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import ru.t1.t1school.aspect.annotation.LogExecution;
import ru.t1.t1school.aspect.annotation.TimeExecutionTracking;
import ru.t1.t1school.dto.NotificationTaskDto;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerListener {
    private final NotificationService notificationService;

    @TimeExecutionTracking
    @LogExecution
    @KafkaListener(containerFactory = "kafkaListenerContainerFactory",
            topics = "${t1.kafka.consumer.topic}",
            groupId = "${t1.kafka.consumer.group-id}")
    public void handleStatusTasksUpdate(List<ConsumerRecord<Long, NotificationTaskDto>> records, Acknowledgment ack) {
        try {
            for (ConsumerRecord<Long, NotificationTaskDto> record : records) {
                try {
                    log.info("Received record: key={}, value={}", record.key(), record.value());
                    notificationService.notifyStatusTaskUpdate(record.value());
                } catch (Exception e) {
                    log.error("Exception record: key={}, value={}: {}",
                            record.key(),
                            record.value(),
                            e.getMessage(), e);
                    // Можно будет положить в очередь отложенных для дальнейшей обработки
                }
            }
            ack.acknowledge(); // Подтверждаем обработку всех успешных сообщений
        } catch (Exception ex) {
            log.error("Exception batch: {}", ex.getMessage(), ex);
            ack.nack(0, Duration.ofMillis(100L)); // Повторная обработка всего пакета
        }
    }
}
