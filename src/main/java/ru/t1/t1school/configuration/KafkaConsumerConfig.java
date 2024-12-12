package ru.t1.t1school.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;
import ru.t1.t1school.dto.NotificationTaskDto;
import ru.t1.t1school.mapper.TaskNotificationDeserializer;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@EnableKafka
@RequiredArgsConstructor
public class KafkaConsumerConfig {
    private final ConsumerProps consumerProps;

    @Bean
    public ConsumerFactory<Long, NotificationTaskDto> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerProps.servers());
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, consumerProps.groupId());
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, TaskNotificationDeserializer.class); // можно написать свой
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "ru.t1.t1school.dto.NotificationTaskDto");// можно написать свой
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        configProps.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, Boolean.FALSE);
        configProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, consumerProps.sessionTimoutMs());
        configProps.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, consumerProps.maxPartitionFetchBytes());
        configProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, consumerProps.maxPollRecords());
        configProps.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, consumerProps.maxPollInterval());
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.FALSE);
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, consumerProps.autoOffsetReset());
        configProps.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class);// можно написать свой
        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class);// можно написать свой

        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<Long, NotificationTaskDto> kafkaListenerContainerFactory(@Qualifier("consumerFactory") ConsumerFactory<Long, NotificationTaskDto> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Long, NotificationTaskDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factoryBuilder(consumerFactory, factory);

        return factory;
    }

    private <T> void factoryBuilder(ConsumerFactory<Long, T> consumerFactory, ConcurrentKafkaListenerContainerFactory<Long, T> factory) {
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true);
        factory.setConcurrency(1);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.getContainerProperties().setPollTimeout(5000);
        factory.getContainerProperties().setMicrometerEnabled(true);
        factory.setCommonErrorHandler(errorHandler());
    }

    private CommonErrorHandler errorHandler() {
        DefaultErrorHandler handler = new DefaultErrorHandler(new FixedBackOff(1000, 3));
        handler.addNotRetryableExceptions(IllegalStateException.class);
        handler.setRetryListeners(((record, ex, deliveryAttempt) -> log.error("Retry listeners message = {}, offset = {}, deliveryAttempt = {}",
                ex.getMessage(), record.offset(), deliveryAttempt)));
        handler.addNotRetryableExceptions(NullPointerException.class);
        return handler;
    }
}
