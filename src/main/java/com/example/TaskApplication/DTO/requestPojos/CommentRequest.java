package com.example.TaskApplication.DTO.requestPojos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentRequest {
    long userId;
    long taskId;
    String comment;
}
