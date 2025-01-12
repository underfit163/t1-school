package ru.t1.t1school.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import ru.t1.t1school.configuration.NotificationProps;
import ru.t1.t1school.dto.NotificationTaskDto;
import ru.t1.t1school.entity.Status;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailNotificationServiceTest {
    @InjectMocks
    private EmailNotificationServiceImpl emailNotificationService;

    @Mock
    private JavaMailSender emailSender;

    @Mock
    private NotificationProps notificationProps;

    @Test
    @DisplayName("Тест проверки на пустой список email")
    void notifyStatusTaskUpdate_shouldLogWarning_whenNoEmailsConfigured() {
        NotificationTaskDto notificationTaskDto = new NotificationTaskDto(1L, Status.OPEN);

        when(notificationProps.emails()).thenReturn(List.of());

        emailNotificationService.notifyStatusTaskUpdate(notificationTaskDto);

        verify(emailSender, never()).send(any(SimpleMailMessage.class));
    }
}