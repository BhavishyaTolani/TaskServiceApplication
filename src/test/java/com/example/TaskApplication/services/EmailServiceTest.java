package com.example.TaskApplication.services;

import com.example.TaskApplication.DTO.events.ApprovedEvent;
import com.example.TaskApplication.DTO.events.ApproverAddedEvent;
import com.example.TaskApplication.DTO.events.TaskApprovedEvent;
import com.example.TaskApplication.DTO.events.TaskCreatedEvent;
import com.example.TaskApplication.exceptions.TaskServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.ApplicationEvent;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private JavaMailSender mailSender;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendEmail_Success() {
        // Arrange
        String to = "test@example.com";
        String subject = "Test Subject";
        String text = "Test message";

        // Mock the mailSender behavior
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        emailService.sendEmail(to, subject, text);

        // Assert
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendEmail_Failure() {
        // Arrange
        String to = "test@example.com";
        String subject = "Test Subject";
        String text = "Test message";

        // Simulate an exception when sending email
        doThrow(new RuntimeException("Mail sending failed")).when(mailSender).send(any(SimpleMailMessage.class));

        // Act & Assert
        assertDoesNotThrow(() -> emailService.sendEmail(to, subject, text)); // The method should not throw TaskServiceException
    }

    @Test
    void testSendNotification_TaskApprovedEvent() throws TaskServiceException {
        // Arrange
        TaskApprovedEvent taskApprovedEvent = new TaskApprovedEvent(this, 1L, "test@example.com");
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        emailService.sendNotification(taskApprovedEvent);

        // Assert
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendNotification_TaskCreatedEvent() throws TaskServiceException {
        // Arrange
        TaskCreatedEvent taskCreatedEvent = new TaskCreatedEvent(this, 1L, "test@example.com");
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        emailService.sendNotification(taskCreatedEvent);

        // Assert
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendNotification_ApproverAddedEvent() throws TaskServiceException {
        // Arrange
        ApproverAddedEvent approverAddedEvent = new ApproverAddedEvent(this, 1L, "test@example.com");
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        emailService.sendNotification(approverAddedEvent);

        // Assert
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendNotification_UnsupportedEvent() {
        // Arrange
        ApplicationEvent unsupportedEvent = new ApprovedEvent(this);

        // Act & Assert
        TaskServiceException exception = assertThrows(TaskServiceException.class, () -> emailService.sendNotification(unsupportedEvent));
        assertEquals("Event not supported", exception.getMessage());
    }

    @Test
    void testSendNotification_EventEmailNotSent() throws TaskServiceException {
        // Arrange
        TaskCreatedEvent taskCreatedEvent = new TaskCreatedEvent(this, 1L, "test@example.com");
        doThrow(new RuntimeException("Mail sending failed")).when(mailSender).send(any(SimpleMailMessage.class));

        // Act & Assert
        assertDoesNotThrow(() -> emailService.sendNotification(taskCreatedEvent));  // It should not throw TaskServiceException here
    }
}

