package com.example.TaskApplication.DTO.events;

import com.example.TaskApplication.utils.Constants;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class TaskApprovedEvent extends ApplicationEvent {

    private long taskId;

    private String subject = Constants.TASK_APPROVED_EVENT_SUBJECT;

    private String message = Constants.TASK_APPROVED_EVENT_MESSAGE;

    private String email;

    public TaskApprovedEvent(Object source, long taskId, String email) {
        super(source);
        this.taskId = taskId;
        this.subject = String.format(subject);
        this.message = String.format(message, taskId);
        this.email = email;
    }

}

