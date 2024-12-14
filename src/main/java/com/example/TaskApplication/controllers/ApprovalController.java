package com.example.TaskApplication.controllers;

import com.example.TaskApplication.DTO.requestPojos.ApprovalRequest;
import com.example.TaskApplication.DTO.responsePojos.ApprovalResponse;
import com.example.TaskApplication.DTO.requestPojos.ApproveRequest;
import com.example.TaskApplication.DTO.responsePojos.RequestApprovalResponse;
import com.example.TaskApplication.exceptions.TaskServiceException;
import com.example.TaskApplication.services.ApprovalService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;

    @PostMapping("/requestForApproval")
    RequestApprovalResponse requestForApproval(@RequestBody ApprovalRequest approvalRequest) {
        try {
             return approvalService.addApprovers(approvalRequest);
        }
        catch (TaskServiceException e) {
            throw new TaskServiceException("Request approval failed : " + e.getMessage());
        }
        catch (Exception e) {
            throw new RuntimeException("Request approval failed : " + e.getMessage());
        }
    }

    @PostMapping("/approve")
    ApprovalResponse approve(@RequestBody ApproveRequest approveRequest) {
        try {
            return approvalService.approve(approveRequest);
        }
        catch (TaskServiceException e) {
            throw new TaskServiceException("Approve Request failed : " + e.getMessage());
        }
        catch (Exception e) {
            throw new RuntimeException("Approval request failed : " + e.getMessage());
        }
    }
}
