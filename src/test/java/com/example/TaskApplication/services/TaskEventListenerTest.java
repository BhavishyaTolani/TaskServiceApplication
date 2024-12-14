package com.example.TaskApplication.services;

import com.example.TaskApplication.DTO.events.ApproverAddedEvent;
import com.example.TaskApplication.DTO.events.TaskApprovedEvent;
import com.example.TaskApplication.DTO.events.TaskCreatedEvent;
import com.example.TaskApplication.exceptions.TaskServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class TaskEventListenerTest {

    @InjectMocks
    private TaskEventListener taskEventListener;

    @Mock
    private EmailService emailService;

    @Mock
    private PageNotificationService pageNotificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleTaskApprovedEvent() throws TaskServiceException {
        // Arrange
        TaskApprovedEvent taskApprovedEvent = new TaskApprovedEvent(this, 1L, "test@example.com");

        // Act
        taskEventListener.handleTaskApprovedEvent(taskApprovedEvent);

        // Assert
        verify(emailService, times(1)).sendNotification(taskApprovedEvent);
    }

    @Test
    void testHandleTaskCreatedEvent() throws TaskServiceException {
        // Arrange
        TaskCreatedEvent taskCreatedEvent = new TaskCreatedEvent(this, 1L, "test@example.com");

        // Act
        taskEventListener.handleTaskCreatedEvent(taskCreatedEvent);

        // Assert
        verify(emailService, times(1)).sendNotification(taskCreatedEvent);
    }

    @Test
    void testHandleApproverRequestedEvent() throws TaskServiceException {
        // Arrange
        ApproverAddedEvent approverAddedEvent = new ApproverAddedEvent(this, 1L, "test@example.com");

        // Act
        taskEventListener.handleApproverRequestedEvent(approverAddedEvent);

        // Assert
        verify(emailService, times(1)).sendNotification(approverAddedEvent);
    }

    @Test
    void testHandleApprovedEvent() throws TaskServiceException {
        // Arrange
        ApproverAddedEvent approverAddedEvent = new ApproverAddedEvent(this, 1L, "test@example.com");

        // Act
        taskEventListener.handleApprovedEvent(approverAddedEvent);

        // Assert
        verify(pageNotificationService, times(1)).sendNotification(approverAddedEvent);
    }

    @Test
    void testHandleTaskApprovedEvent_Exception() {
        // Arrange
        TaskApprovedEvent taskApprovedEvent = new TaskApprovedEvent(this, 1L, "test@example.com");
        doThrow(new TaskServiceException("Error")).when(emailService).sendNotification(any());

        // Act & Assert
        TaskServiceException exception = assertThrows(TaskServiceException.class, () -> taskEventListener.handleTaskApprovedEvent(taskApprovedEvent));
        assertEquals("Error", exception.getMessage());
    }

    @Test
    void testHandleTaskCreatedEvent_Exception() {
        // Arrange
        TaskCreatedEvent taskCreatedEvent = new TaskCreatedEvent(this, 1L, "test@example.com");
        doThrow(new TaskServiceException("Error")).when(emailService).sendNotification(any());

        // Act & Assert
        TaskServiceException exception = assertThrows(TaskServiceException.class, () -> taskEventListener.handleTaskCreatedEvent(taskCreatedEvent));
        assertEquals("Error", exception.getMessage());
    }

    @Test
    void testHandleApproverRequestedEvent_Exception() {
        // Arrange
        ApproverAddedEvent approverAddedEvent = new ApproverAddedEvent(this, 1L, "test@example.com");
        doThrow(new TaskServiceException("Error")).when(emailService).sendNotification(any());

        // Act & Assert
        TaskServiceException exception = assertThrows(TaskServiceException.class, () -> taskEventListener.handleApproverRequestedEvent(approverAddedEvent));
        assertEquals("Error", exception.getMessage());
    }

    @Test
    void testHandleApprovedEvent_Exception() {
        // Arrange
        ApproverAddedEvent approverAddedEvent = new ApproverAddedEvent(this, 1L, "test@example.com");
        doThrow(new TaskServiceException("Error")).when(pageNotificationService).sendNotification(any());

        // Act & Assert
        TaskServiceException exception = assertThrows(TaskServiceException.class, () -> taskEventListener.handleApprovedEvent(approverAddedEvent));
        assertEquals("Error", exception.getMessage());
    }
}
