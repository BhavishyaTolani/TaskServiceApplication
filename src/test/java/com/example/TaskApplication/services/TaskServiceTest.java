package com.example.TaskApplication.services;

import com.example.TaskApplication.DTO.events.TaskApprovedEvent;
import com.example.TaskApplication.DTO.events.TaskCreatedEvent;
import com.example.TaskApplication.DTO.requestPojos.TaskRequest;
import com.example.TaskApplication.DTO.responsePojos.TaskResponse;
import com.example.TaskApplication.DTO.status.ApprovalStatus;
import com.example.TaskApplication.DTO.status.TaskStatus;
import com.example.TaskApplication.entities.Task;
import com.example.TaskApplication.entities.TaskApproval;
import com.example.TaskApplication.entities.User;
import com.example.TaskApplication.exceptions.TaskServiceException;
import com.example.TaskApplication.repositories.TaskApproverRepository;
import com.example.TaskApplication.repositories.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskApproverRepository taskApproverRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private User user;

    @Mock
    private Task task;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ApplicationContext.setCurrentUser(2L);
        ReflectionTestUtils.setField(taskService, "approversCount", 3);
    }

    @Test
    void testGetTask_TaskExists() throws TaskServiceException {
        long taskId = 1L;
        when(taskRepository.findById(taskId)).thenReturn(java.util.Optional.of(task));
        when(task.getStatus()).thenReturn(TaskStatus.CREATED);
        when(task.getDescription()).thenReturn("Test Task");
        when(task.getId()).thenReturn(taskId);
        when(task.getCreatedBy()).thenReturn(user);
        when(user.getId()).thenReturn(1L);

        TaskResponse taskResponse = taskService.getTask(taskId);

        assertNotNull(taskResponse);
        assertEquals(TaskStatus.CREATED, taskResponse.getTaskStatus());
        assertEquals("Test Task", taskResponse.getDescription());
    }

    @Test
    void testGetTask_TaskDoesNotExist() {
        long taskId = 1L;
        when(taskRepository.findById(taskId)).thenReturn(java.util.Optional.empty());

        TaskServiceException exception = assertThrows(TaskServiceException.class, () -> taskService.getTask(taskId));
        assertEquals("Task Does Not Exist", exception.getMessage());
    }

    @Test
    void testCreateTask_Success() throws TaskServiceException {
        long userId = 2L;
        TaskRequest taskRequest = TaskRequest.builder().userId(userId).description("Test Task").build();
        Task task = new Task(userId, TaskStatus.CREATED, "Test Task");
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(user.getId()).thenReturn(userId);

        TaskResponse taskResponse = taskService.createTask(taskRequest);

        assertNotNull(taskResponse);
        assertEquals("Test Task", taskResponse.getDescription());
        assertEquals(TaskStatus.CREATED, taskResponse.getTaskStatus());
        verify(eventPublisher, times(1)).publishEvent(any(TaskCreatedEvent.class));
    }

    @Test
    void testCreateTask_InvalidUser() {
        long userId = 1L;
        TaskRequest taskRequest = TaskRequest.builder().userId(userId).description("Test Task").build();

        TaskServiceException exception = assertThrows(TaskServiceException.class, () -> taskService.createTask(taskRequest));
        assertEquals("Invalid user for the create task request. Logged in user id does not match with task creater id", exception.getMessage());
    }

    @Test
    void testUpdateApproversCount_ApprovalsNotFull() {
        long taskId = 1L;
        when(taskRepository.findById(taskId)).thenReturn(java.util.Optional.of(task));
        when(task.getApprovalCount()).thenReturn(2);
        when(task.getStatus()).thenReturn(TaskStatus.CREATED);
        when(task.getApprovalCount()).thenReturn(2);
        when(task.getCreatedBy()).thenReturn(user);
        when(user.getEmail()).thenReturn("test@example.com");

        taskService.updateApproversCount(taskId);

        verify(taskRepository, times(1)).save(task);  // Task should be saved even if approval count is not full
        verify(eventPublisher, times(0)).publishEvent(any(TaskApprovedEvent.class));  // Event shouldn't be triggered yet
    }

    @Test
    void testUpdateApproversCount_ApprovalsFull() {
        long taskId = 1L;
        int approversCount = 2;

        User creatorUser = new User();
        creatorUser.setId(1L);
        creatorUser.setEmail("test@example.com");

        User approverUser = new User();
        approverUser.setId(2L);
        approverUser.setEmail("approver@example.com");

        Task task = new Task();
        task.setId(taskId);
        task.setStatus(TaskStatus.CREATED);
        task.setApprovalCount(approversCount - 1);
        task.setDescription("Test task description");
        task.setCreatedBy(creatorUser);

        TaskApproval taskApproval = new TaskApproval();
        taskApproval.setId(1L);
        taskApproval.setTask(task);
        taskApproval.setApprover(approverUser);
        taskApproval.setStatus(ApprovalStatus.PENDING);

        List<TaskApproval> taskApprovals = List.of(taskApproval);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskApproverRepository.findAllByTaskId(taskId)).thenReturn(taskApprovals);
        taskService.setApproversCount(approversCount);
        doNothing().when(eventPublisher).publishEvent(any());

        taskService.updateApproversCount(taskId);

        verify(taskRepository, times(1)).save(task);
        assertEquals(TaskStatus.APPROVED, task.getStatus());
        verify(eventPublisher, times(1)).publishEvent(argThat(event -> event instanceof TaskApprovedEvent
                && ((TaskApprovedEvent) event).getTaskId() == taskId
                && ((TaskApprovedEvent) event).getEmail().equals(creatorUser.getEmail())));
        verify(eventPublisher, times(1)).publishEvent(argThat(event -> event instanceof TaskApprovedEvent
                && ((TaskApprovedEvent) event).getTaskId() == taskId
                && ((TaskApprovedEvent) event).getEmail().equals(approverUser.getEmail())));
    }

}

