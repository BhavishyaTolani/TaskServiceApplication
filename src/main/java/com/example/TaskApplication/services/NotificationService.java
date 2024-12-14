package com.example.TaskApplication.services;

import com.example.TaskApplication.exceptions.TaskServiceException;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Service;

@Service
public interface NotificationService {
    public void sendNotification(ApplicationEvent event) throws TaskServiceException;
}
