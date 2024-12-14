package com.example.TaskApplication.DTO.requestPojos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ApproveRequest {
    long approverId;
    long taskId;
}
