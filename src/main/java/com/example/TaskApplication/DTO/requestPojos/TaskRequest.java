package com.example.TaskApplication.DTO.requestPojos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskRequest {
    long userId;
    String description;
}
