package com.example.TaskApplication.DTO.responsePojos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentResponse {
    private long commentId;
    private long userId;
    private long taskId;
    private String comment;
    private String commentResponse;
}
