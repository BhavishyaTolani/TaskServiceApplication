package com.example.TaskApplication.DTO.responsePojos;

import com.example.TaskApplication.DTO.status.TaskStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskResponse {
    private long id;
    private long userId;
    private TaskStatus taskStatus;
    private String description;
    private String taskResponse;
}
