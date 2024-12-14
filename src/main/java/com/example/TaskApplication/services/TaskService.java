package com.example.TaskApplication.services;

import com.example.TaskApplication.DTO.events.TaskApprovedEvent;
import com.example.TaskApplication.DTO.events.TaskCreatedEvent;
import com.example.TaskApplication.DTO.requestPojos.TaskRequest;
import com.example.TaskApplication.DTO.responsePojos.TaskResponse;
import com.example.TaskApplication.DTO.status.TaskStatus;
import com.example.TaskApplication.entities.Task;
import com.example.TaskApplication.entities.TaskApproval;
import com.example.TaskApplication.entities.User;
import com.example.TaskApplication.exceptions.TaskServiceException;
import com.example.TaskApplication.repositories.TaskApproverRepository;
import com.example.TaskApplication.repositories.TaskRepository;
import com.example.TaskApplication.utils.Constants;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Data
public class TaskService {

    private final TaskRepository taskRepository;

    private final TaskApproverRepository taskApproverRepository;

    private final ApplicationEventPublisher eventPublisher;

    @Value("${approval-config.maxApprovers}")
    private int approversCount;

    public TaskResponse getTask(long id) throws TaskServiceException {
        Optional<Task> task = taskRepository.findById(id);
        if(task.isEmpty())
            throw new TaskServiceException("Task Does Not Exist");
        TaskResponse taskResponse = TaskResponse.builder()
                .taskStatus(task.get().getStatus())
                .description(task.get().getDescription())
                .id(task.get().getId())
                .userId(task.get().getCreatedBy().getId())
                .taskResponse(Constants.GET_TASK_SUCCESS_MESSAGE)
                .build();
        return taskResponse;
    }

    public TaskResponse createTask(TaskRequest taskRequest) throws TaskServiceException {
        if(!Objects.equals(taskRequest.getUserId(), ApplicationContext.getCurrentUser()))
            throw new TaskServiceException("Invalid user for the create task request. Logged in user id does not match with task creater id");

        Task task = taskRepository.save(new Task(
                taskRequest.getUserId(),
                TaskStatus.CREATED,
                taskRequest.getDescription()));

        TaskCreatedEvent event = new TaskCreatedEvent(this, task.getId(), ApplicationContext.getCurrentUserEmail());

        eventPublisher.publishEvent(event);

        return TaskResponse.builder()
                .taskStatus(task.getStatus())
                .description(task.getDescription())
                .id(task.getId())
                .userId(task.getCreatedBy().getId())
                .taskResponse(String.format(Constants.TASK_CREATED_EVENT_MESSAGE, task.getId()))
                .build();

    }
    public void updateApproversCount(long taskId) {
        Task task = taskRepository.findById(taskId).get();
        task.setApprovalCount(task.getApprovalCount() + 1);

        if(Objects.equals(task.getApprovalCount(), approversCount)) {
            task.setStatus(TaskStatus.APPROVED);
            task = taskRepository.save(task);
            eventPublisher.publishEvent(new TaskApprovedEvent(this, taskId, task.getCreatedBy().getEmail()));
            List<TaskApproval> taskApprovals = taskApproverRepository.findAllByTaskId(task.getId());
            for(TaskApproval taskApproval : taskApprovals) {
                User approver = taskApproval.getApprover();
                eventPublisher.publishEvent(new TaskApprovedEvent(this, taskId, approver.getEmail()));
            }
        }
        else {
            taskRepository.save(task);
        }


    }
}
