package com.example.TaskApplication.DTO.responsePojos;

import com.example.TaskApplication.utils.Constants;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RequestApprovalResponse {
    private long taskId;
    private List<Long> approvers;
    private final String approvalAdded = Constants.APPROVERS_ADDED_FOR_TASK_MESSAGE;
}
