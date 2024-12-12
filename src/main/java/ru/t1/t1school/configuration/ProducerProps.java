package ru.t1.t1school.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "t1.kafka.producer")
public record ProducerProps(String servers, String topic, String asks) {
}
