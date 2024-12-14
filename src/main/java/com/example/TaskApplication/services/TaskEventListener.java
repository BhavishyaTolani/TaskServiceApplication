package com.example.TaskApplication.services;

import com.example.TaskApplication.DTO.events.ApproverAddedEvent;
import com.example.TaskApplication.DTO.events.TaskApprovedEvent;
import com.example.TaskApplication.DTO.events.TaskCreatedEvent;
import com.example.TaskApplication.exceptions.TaskServiceException;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@Data
public class TaskEventListener {

    @Autowired
    NotificationService notificationService;

    private final EmailService emailService;

    private final PageNotificationService pageNotificationService;

    @Autowired
    public TaskEventListener(EmailService emailService, PageNotificationService pageNotificationService) {
        this.emailService = emailService;
        this.pageNotificationService = pageNotificationService;
    }

    @EventListener
    public void handleTaskApprovedEvent(TaskApprovedEvent event) throws TaskServiceException {
        this.setNotificationService(emailService);
        notificationService.sendNotification(event);
    }

    @EventListener
    public void handleTaskCreatedEvent(TaskCreatedEvent event) throws TaskServiceException {
        this.setNotificationService(emailService);
        notificationService.sendNotification(event);

    }

    @EventListener
    public void handleApproverRequestedEvent(ApproverAddedEvent event) throws TaskServiceException {
        this.setNotificationService(emailService);
        notificationService.sendNotification(event);

    }

    @EventListener
    public void handleApprovedEvent(ApproverAddedEvent event) throws TaskServiceException {
        this.setNotificationService(pageNotificationService);
        notificationService.sendNotification(event);

    }
}
