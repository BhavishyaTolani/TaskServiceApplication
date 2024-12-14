package com.example.TaskApplication.services;

import com.example.TaskApplication.DTO.events.TaskApprovedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.slf4j.Logger;
import org.springframework.context.ApplicationEvent;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class PageNotificationServiceTest {

    @InjectMocks
    private PageNotificationService pageNotificationService;

    @Mock
    private Logger logger;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendNotification_TaskApprovedEvent() {
        TaskApprovedEvent taskApprovedEvent = new TaskApprovedEvent(this, 1L, "test@example.com");

        doNothing().when(logger).info(anyString());

        pageNotificationService.sendNotification(taskApprovedEvent);

        verify(logger, times(1)).info(eq("Page notification for task={} approved by user={}"), eq(taskApprovedEvent));
    }

    @Test
    void testSendNotification_CastException() {
        // Arrange
        ApplicationEvent unsupportedEvent = mock(ApplicationEvent.class);

        // Act & Assert
        assertThrows(ClassCastException.class, () -> pageNotificationService.sendNotification(unsupportedEvent));
    }
}
