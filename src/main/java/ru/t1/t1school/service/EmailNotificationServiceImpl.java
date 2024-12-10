package ru.t1.t1school.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.t1.t1school.aspect.annotation.LogExecution;
import ru.t1.t1school.configuration.NotificationProps;
import ru.t1.t1school.dto.NotificationTaskDto;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotificationServiceImpl implements NotificationService {
    private final NotificationProps notificationProps;
    private final JavaMailSender emailSender;

    @LogExecution
    @Override
    @Async("taskExecutor")
    public void notifyStatusTaskUpdate(NotificationTaskDto notificationTaskDto) {
        try {
            String emailText = String.format("Task %d status updated to %s",
                    notificationTaskDto.id(),
                    notificationTaskDto.status());

            log.info("Notifying: {}", emailText);

            List<String> emails = notificationProps.emails();
            if (emails != null && !emails.isEmpty())
                notificationProps.emails().forEach(email ->
                        sendEmail(email, "Task %d update notification".formatted(notificationTaskDto.id()), emailText));
            else log.info("No emails found for notification task");
        } catch (Exception e) {
            log.error("Exception send email: {}", e.getMessage(), e);
        }
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }
}
