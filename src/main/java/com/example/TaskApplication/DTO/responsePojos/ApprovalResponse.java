package com.example.TaskApplication.DTO.responsePojos;

import com.example.TaskApplication.DTO.status.ApprovalStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApprovalResponse {
    private ApprovalStatus approvalStatus;
    private long taskId;
    private String approvalResponse;
}
