package ru.t1.t1school.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import ru.t1.logstarter.aspect.annotation.LogExecution;
import ru.t1.t1school.configuration.ProducerProps;
import ru.t1.t1school.dto.NotificationTaskDto;


@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerServiceImpl implements KafkaProducerService {
    private final ProducerProps producerProps;
    private final KafkaTemplate<Long, NotificationTaskDto> kafkaTemplate;

    @LogExecution
    @Override
    public void
    sendStatusTaskUpdate(NotificationTaskDto notificationTaskDto) {
        try {
            kafkaTemplate.send(producerProps.topic(), notificationTaskDto.id(), notificationTaskDto)
                    .whenComplete((longNotificationTaskDtoSendResult, throwable) -> {
                        if (throwable == null) {
                            log.info("Message sent: key={}, value={}", notificationTaskDto.id(), notificationTaskDto);
                        } else {
                            log.error("Failed to send message: key={}, value={}",
                                    notificationTaskDto.id(), notificationTaskDto, throwable);
                        }
                    });
        } catch (Exception e) {
            log.error("Error sending message to Kafka: {}", e.getMessage(), e);
        }
    }
}
