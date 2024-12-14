package com.example.TaskApplication.DTO.events;

import com.example.TaskApplication.utils.Constants;
import lombok.Data;
import org.springframework.context.ApplicationEvent;


@Data
public class ApprovedEvent extends ApplicationEvent {

    private long taskId;

    private long approverId;

    private String subject = Constants.APPROVER_ADDED_EVENT_SUBJECT;

    private String message = Constants.APPROVER_ADDED_EVENT_MESSAGE;

    public ApprovedEvent(Object source, long taskId, long approverId) {
        super(source);
        this.taskId = taskId;
        this.message = String.format(message, taskId);
        this.subject = getSubject();
        this.approverId = approverId;
    }

    public ApprovedEvent(Object source) {
        super(source);
    }

}
