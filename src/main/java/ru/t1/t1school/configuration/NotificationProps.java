package ru.t1.t1school.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "t1.notification")
public record NotificationProps(List<String> emails) {
}
