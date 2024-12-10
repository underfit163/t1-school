package ru.t1.t1school.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "t1.kafka.consumer")
public record ConsumerProps(String servers,
                            String topic,
                            String groupId,
                            Integer maxPollRecords,
                            Integer maxPartitionFetchBytes,
                            Integer maxPollInterval,
                            Integer sessionTimoutMs,
                            String autoOffsetReset) {
}
