package com.example.TaskApplication.DTO.events;

import com.example.TaskApplication.utils.Constants;
import lombok.Data;
import org.springframework.context.ApplicationEvent;


@Data
public class ApproverAddedEvent extends ApplicationEvent {

    private long taskId;

    private final String subject = Constants.APPROVER_ADDED_EVENT_SUBJECT;

    private String message = Constants.APPROVER_ADDED_EVENT_MESSAGE;

    private String email;

    public ApproverAddedEvent(Object source, long taskId, String email) {
        super(source);
        this.taskId = taskId;
        this.message = String.format(message, taskId);
        this.email = email;
    }

}
