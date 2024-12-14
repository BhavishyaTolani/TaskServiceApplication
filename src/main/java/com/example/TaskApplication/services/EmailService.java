package com.example.TaskApplication.services;

import com.example.TaskApplication.DTO.events.ApproverAddedEvent;
import com.example.TaskApplication.DTO.events.TaskApprovedEvent;
import com.example.TaskApplication.DTO.events.TaskCreatedEvent;
import com.example.TaskApplication.exceptions.TaskServiceException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Primary
@Slf4j
public class EmailService implements NotificationService {

    private final JavaMailSender mailSender;

    Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("hello@demomailtrap.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        try {
            mailSender.send(message);
            logger.info("Email sent successfully");
        } catch (Exception e) {
            logger.error("Error sending email: {}", e.getMessage());
        }
    }

    @Override
    public void sendNotification(ApplicationEvent event) throws TaskServiceException {
        if(event instanceof TaskApprovedEvent) {
            TaskApprovedEvent taskApprovedEvent = (TaskApprovedEvent) event;
            sendEmail(taskApprovedEvent.getEmail(), taskApprovedEvent.getSubject(), taskApprovedEvent.getMessage());
        }
        else if(event instanceof TaskCreatedEvent) {
            TaskCreatedEvent taskCreatedEvent = (TaskCreatedEvent) event;
            sendEmail(taskCreatedEvent.getEmail(), taskCreatedEvent.getSubject(), taskCreatedEvent.getMessage());
        }
        else if(event instanceof ApproverAddedEvent) {
            ApproverAddedEvent approverAddedEvent = (ApproverAddedEvent) event;
            sendEmail(approverAddedEvent.getEmail(), approverAddedEvent.getSubject(), approverAddedEvent.getMessage());
        }
        else {
            throw new TaskServiceException("Event not supported");
        }
    }
}