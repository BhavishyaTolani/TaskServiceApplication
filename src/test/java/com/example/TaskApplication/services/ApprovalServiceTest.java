package com.example.TaskApplication.services;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApprovalServiceTest {

    @InjectMocks
    private ApprovalService approvalService;

    @Mock
    private TaskApproverRepository taskApproverRepository;

    @Mock
    private UserService userService;

    @Mock
    private TaskService taskService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private TaskApproval taskApproval;
    private User approver;
    private User taskCreator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        taskApproval = new TaskApproval(1L, 2L, ApprovalStatus.PENDING);

        taskCreator = new User(1L, "SampleUser", "creator@example.com", "password");
        approver = new User(2L, "SampleUser2", "approver@example.com", "password");
        ReflectionTestUtils.setField(approvalService, "approversCount", 3);
        ApplicationContext.setCurrentUser(2L);
    }

    @Test
    void testAddApprovers_ValidScenario() throws TaskServiceException {
        // Arrange
        ApprovalRequest approvalRequest = ApprovalRequest.builder().taskId(1L).approvers(Arrays.asList(2L, 3L, 4L)).build();
        when(taskService.getTask(1L)).thenReturn(TaskResponse.builder().id(1L).userId(taskCreator.getId()).build()); // Mocking Task Service
        when(userService.getUser(2L)).thenReturn(Optional.of(approver));
        when(userService.getUser(3L)).thenReturn(Optional.of(new User(3L, "SampleUser3", "approver2@example.com", "password")));
        when(userService.getUser(4L)).thenReturn(Optional.of(new User(4L, "SampleUser4","approver3@example.com", "password")));

        TaskApproval taskApproval2 = new TaskApproval(2L, 2L, ApprovalStatus.PENDING);
        TaskApproval taskApproval3 = new TaskApproval(3L, 2L, ApprovalStatus.PENDING);

        when(taskApproverRepository.saveAll(anyList())).thenReturn(Arrays.asList(taskApproval, taskApproval2, taskApproval3));

        RequestApprovalResponse response = approvalService.addApprovers(approvalRequest);

        assertNotNull(response);
        assertEquals(3, response.getApprovers().size());
        verify(eventPublisher, times(3)).publishEvent(any());
        verify(taskApproverRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testAddApprovers_ApproversCountMismatch() {
        // Arrange
        ApprovalRequest approvalRequest = ApprovalRequest.builder().taskId(1L).approvers(Arrays.asList(2L, 3L)).build();

        // Act & Assert
        TaskServiceException exception = assertThrows(TaskServiceException.class, () -> approvalService.addApprovers(approvalRequest));
        assertEquals("Approvers numbers mismatch", exception.getMessage());
    }

    @Test
    void testAddApprovers_InvalidApprover() {
        // Arrange
        ApprovalRequest approvalRequest = ApprovalRequest.builder().taskId(1L).approvers(Arrays.asList(2L, 3L, 4L)).build();
        when(taskService.getTask(1L)).thenReturn(TaskResponse.builder().id(1L).userId(taskCreator.getId()).build());
        when(userService.getUser(2L)).thenReturn(Optional.of(approver));
        when(userService.getUser(3L)).thenReturn(Optional.empty());
        when(userService.getUser(4L)).thenReturn(Optional.of(new User(4L, "SampleUser4","approver3@example.com", "password")));

        // Act & Assert
        TaskServiceException exception = assertThrows(TaskServiceException.class, () -> approvalService.addApprovers(approvalRequest));
        assertEquals("Approver is not valid", exception.getMessage());
    }

    @Test
    void testAddApprovers_CreatorIsApprover() {
        ApprovalRequest approvalRequest = ApprovalRequest.builder().taskId(1L).approvers(Arrays.asList(2L, 3L, 4L)).build();
        when(taskService.getTask(1L)).thenReturn(TaskResponse.builder().id(1L).userId(2L).build());
        when(userService.getUser(2L)).thenReturn(Optional.of(approver));

        TaskServiceException exception = assertThrows(TaskServiceException.class, () -> approvalService.addApprovers(approvalRequest));
        assertEquals("Creater of the task can not be an approver", exception.getMessage());
    }

    @Test
    void testApprove_ValidScenario() throws TaskServiceException {
        ApproveRequest approveRequest = new ApproveRequest(2L, 1L); // Approve the task
        when(taskService.getTask(1L)).thenReturn(TaskResponse.builder().id(1L).userId(taskCreator.getId()).build());
        when(taskApproverRepository.findByTaskIdAndApproverId(1L, 2L)).thenReturn(Optional.of(taskApproval));
        when(taskApproverRepository.save(any(TaskApproval.class))).thenReturn(taskApproval);

        ApprovalResponse response = approvalService.approve(approveRequest);

        assertNotNull(response);
        assertEquals(ApprovalStatus.APPROVED, response.getApprovalStatus());
        verify(eventPublisher, times(1)).publishEvent(any());
        verify(taskApproverRepository, times(1)).save(any());
    }

    @Test
    void testApprove_InvalidApprover() {
        ApplicationContext.setCurrentUser(0L);
        ApproveRequest approveRequest = new ApproveRequest(2L, 1L);
        when(taskService.getTask(1L)).thenReturn(TaskResponse.builder().id(1L).userId(taskCreator.getId()).build());

        TaskServiceException exception = assertThrows(TaskServiceException.class, () -> approvalService.approve(approveRequest));
        assertEquals("Invalid user for the approve request. Logged in user id does not match with approver id", exception.getMessage());
        ApplicationContext.setCurrentUser(2L);
    }

    @Test
    void testApprove_NoTaskApprovalFound() {
        // Arrange
        ApproveRequest approveRequest = new ApproveRequest(2L, 1L);
        when(taskService.getTask(1L)).thenReturn(TaskResponse.builder().id(1L).userId(taskCreator.getId()).build());
        when(taskApproverRepository.findByTaskIdAndApproverId(1L, 2L)).thenReturn(Optional.empty());

        // Act & Assert
        TaskServiceException exception = assertThrows(TaskServiceException.class, () -> approvalService.approve(approveRequest));
        assertEquals("No valid task approval process found for the given taskId and approverId", exception.getMessage());
    }

    @Test
    void testApprove_AlreadyApproved() {
        ApproveRequest approveRequest = new ApproveRequest(2L, 1L);
        taskApproval.setStatus(ApprovalStatus.APPROVED);
        when(taskService.getTask(1L)).thenReturn(TaskResponse.builder().id(1L).userId(taskCreator.getId()).build());
        when(taskApproverRepository.findByTaskIdAndApproverId(1L, 2L)).thenReturn(Optional.of(taskApproval));

        TaskServiceException exception = assertThrows(TaskServiceException.class, () -> approvalService.approve(approveRequest));
        assertEquals("Task is already approved by the current user", exception.getMessage());
    }

    @Test
    void testAddApprovers_TaskNotFound() {
        // Arrange
        ApprovalRequest approvalRequest = ApprovalRequest.builder().taskId(1L).approvers(Arrays.asList(2L, 3L, 4L)).build();
        when(taskService.getTask(1L)).thenThrow(new TaskServiceException("Task not found"));

        // Act & Assert
        TaskServiceException exception = assertThrows(TaskServiceException.class, () -> approvalService.addApprovers(approvalRequest));
        assertEquals("Task not found", exception.getMessage());
    }
}
