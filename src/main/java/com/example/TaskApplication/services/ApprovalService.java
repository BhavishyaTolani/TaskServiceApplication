package com.example.TaskApplication.services;

import com.example.TaskApplication.DTO.events.ApprovedEvent;
import com.example.TaskApplication.DTO.events.ApproverAddedEvent;
import com.example.TaskApplication.DTO.requestPojos.ApprovalRequest;
import com.example.TaskApplication.DTO.requestPojos.ApproveRequest;
import com.example.TaskApplication.DTO.responsePojos.ApprovalResponse;
import com.example.TaskApplication.DTO.responsePojos.RequestApprovalResponse;
import com.example.TaskApplication.DTO.responsePojos.TaskResponse;
import com.example.TaskApplication.DTO.status.ApprovalStatus;
import com.example.TaskApplication.entities.TaskApproval;
import com.example.TaskApplication.entities.User;
import com.example.TaskApplication.exceptions.TaskServiceException;
import com.example.TaskApplication.repositories.TaskApproverRepository;
import jakarta.transaction.Transactional;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Data
public class ApprovalService {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private TaskApproverRepository taskApproverRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @Value("${approval-config.maxApprovers}")
    private int approversCount;


    public RequestApprovalResponse addApprovers(ApprovalRequest approvalRequest) throws TaskServiceException {
        if(approvalRequest.getApprovers().size() != approversCount)
            throw new TaskServiceException("Approvers numbers mismatch");
        List<TaskApproval> taskApprovals = new ArrayList<>();

        TaskResponse task = taskService.getTask(approvalRequest.getTaskId());

        for (long userId : approvalRequest.getApprovers()) {
            Optional<User> approver = userService.getUser(userId);
            if(approver.isEmpty())
                throw new TaskServiceException("Approver is not valid");
            if(Objects.equals(userId, task.getUserId()))
                throw new TaskServiceException("Creater of the task can not be an approver");
            taskApprovals.add(
                    new TaskApproval(approvalRequest.getTaskId(),
                            userId,
                            ApprovalStatus.PENDING));
            ApproverAddedEvent event = new ApproverAddedEvent(this, task.getId(), approver.get().getEmail());

            eventPublisher.publishEvent(event);
        }
        List<TaskApproval> response = taskApproverRepository.saveAll(taskApprovals);
        RequestApprovalResponse requestApprovalResponse = RequestApprovalResponse.builder()
                .approvers(response.stream().map(approver -> approver.getApprover().getId()).collect(Collectors.toUnmodifiableList()))
                .taskId(response.stream().filter(approval -> Objects.nonNull(approval.getTask().getId())).findFirst().get().getTask().getId())
                .build();
        return requestApprovalResponse;
    }

    @Transactional
    public ApprovalResponse approve(ApproveRequest approveRequest) throws TaskServiceException {
        if(!Objects.equals(ApplicationContext.getCurrentUser(), approveRequest.getApproverId()))
            throw new TaskServiceException("Invalid user for the approve request. Logged in user id does not match with approver id");

        TaskResponse task = taskService.getTask(approveRequest.getTaskId());
        Optional<TaskApproval> taskApproval = taskApproverRepository.findByTaskIdAndApproverId(
                approveRequest.getTaskId(), approveRequest.getApproverId());
        if(taskApproval.isEmpty()) {
            throw new TaskServiceException("No valid task approval process found for the given taskId and approverId");
        }
        TaskApproval validTaskApproval = taskApproval.get();
        if(Objects.equals(validTaskApproval.getStatus(), ApprovalStatus.APPROVED)) {
            throw new TaskServiceException("Task is already approved by the current user");
        }
        validTaskApproval.setStatus(ApprovalStatus.APPROVED);
        validTaskApproval = taskApproverRepository.save(taskApproval.get());

        taskService.updateApproversCount(approveRequest.getTaskId());
        ApprovedEvent event = new ApprovedEvent(this, task.getId(), validTaskApproval.getApprover().getId());

        eventPublisher.publishEvent(event);

        return ApprovalResponse.builder().approvalStatus(validTaskApproval.getStatus()).taskId(validTaskApproval.getId()).build();
    }

}
