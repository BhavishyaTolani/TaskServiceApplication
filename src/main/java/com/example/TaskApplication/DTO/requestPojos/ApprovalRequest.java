package com.example.TaskApplication.DTO.requestPojos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ApprovalRequest {
    long taskId;
    List<Long> approvers;
}
