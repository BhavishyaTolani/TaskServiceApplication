package com.example.TaskApplication.services;

import com.example.TaskApplication.DTO.events.TaskApprovedEvent;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PageNotificationService implements NotificationService {

    Logger logger = LoggerFactory.getLogger(PageNotificationService.class);

    @Override
    public void sendNotification(ApplicationEvent event) {
        TaskApprovedEvent taskApprovedEvent = (TaskApprovedEvent) event;
        logger.info("Page notification for task={} approved by user={}", taskApprovedEvent);

    }
}
