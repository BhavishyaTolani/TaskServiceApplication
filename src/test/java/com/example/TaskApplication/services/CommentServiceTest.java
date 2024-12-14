package com.example.TaskApplication.services;

import com.example.TaskApplication.DTO.requestPojos.CommentRequest;
import com.example.TaskApplication.DTO.responsePojos.CommentResponse;
import com.example.TaskApplication.DTO.responsePojos.TaskResponse;
import com.example.TaskApplication.entities.Comment;
import com.example.TaskApplication.exceptions.TaskServiceException;
import com.example.TaskApplication.repositories.CommentRepository;
import com.example.TaskApplication.utils.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private TaskService taskService;

    @Mock
    private CommentRepository commentRepository;

    private TaskResponse taskResponse;
    private Comment comment;
    private CommentRequest commentRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        taskResponse = TaskResponse.builder()
                .id(1L)
                .userId(2L)
                .taskStatus(null) // Assuming no status in this case, adjust if needed
                .description("Sample task description")
                .taskResponse("Sample task response")
                .build();

        comment = new Comment(1L, "This is a comment", 2L);
        comment.setId(1L); // setting the comment ID after saving

        commentRequest = CommentRequest.builder()
                .taskId(1L)
                .userId(2L)
                .comment("This is a comment")
                .build();

        when(taskService.getTask(1L)).thenReturn(taskResponse);
    }

    @Test
    void testAddComment_ValidScenario() throws TaskServiceException {
        ApplicationContext.setCurrentUser(2L);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentResponse response = commentService.addComment(commentRequest);

        assertNotNull(response);
        assertEquals(Constants.COMMENT_ADDED_RESPONSE_MESSAGE, response.getCommentResponse());
        assertEquals(comment.getId(), response.getCommentId());
        assertEquals(comment.getTask().getId(), response.getTaskId());
        assertEquals(comment.getUser().getId(), response.getUserId());

        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void testAddComment_InvalidUser() {
        commentRequest = CommentRequest.builder()
                .taskId(1L)
                .userId(999L)  // Invalid user ID
                .comment("This is a comment")
                .build();

        TaskServiceException exception = assertThrows(TaskServiceException.class, () -> commentService.addComment(commentRequest));
        assertEquals("Invalid user for the add comment call. Logged in user id does not match with the user id used in comment", exception.getMessage());
    }

    @Test
    void testViewComment_ValidComment() throws TaskServiceException {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        CommentResponse response = commentService.viewComment(1L);

        assertNotNull(response);
        assertEquals(comment.getContent(), response.getComment());
        assertEquals(comment.getTask().getId(), response.getTaskId());
        assertEquals(comment.getUser().getId(), response.getUserId());
        assertEquals(Constants.GET_COMMENT_SUCCESS_MESSAGE, response.getCommentResponse());
    }

    @Test
    void testViewComment_CommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        TaskServiceException exception = assertThrows(TaskServiceException.class, () -> commentService.viewComment(1L));
        assertEquals("Comment does not exist", exception.getMessage());
    }

    @Test
    void testViewAllCommentsForTask_ValidScenario() throws TaskServiceException {
        List<Comment> comments = Arrays.asList(
                new Comment(1L, "Comment 1", 2L),
                new Comment(1L, "Comment 2", 3L)
        );
        comments.get(0).setCreatedDate(new Timestamp(1L));
        comments.get(1).setCreatedDate(new Timestamp(2L));
        when(commentRepository.findAllByTaskId(1L)).thenReturn(comments);

        List<CommentResponse> responses = commentService.viewAllCommentsForTask(1L);

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Comment 1", responses.get(0).getComment());
        assertEquals("Comment 2", responses.get(1).getComment());
    }

    @Test
    void testViewAllCommentsForTask_NoComments() throws TaskServiceException {
        when(commentRepository.findAllByTaskId(1L)).thenReturn(Collections.emptyList());

        List<CommentResponse> responses = commentService.viewAllCommentsForTask(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());  // Should return the "No comments" message
        assertEquals(String.format(Constants.NO_COMMENTS_EXISTS, 1L), responses.get(0).getCommentResponse());
    }

    @Test
    void testViewAllCommentsForTask_TaskNotFound() {
        when(taskService.getTask(1L)).thenThrow(new TaskServiceException("Task not found"));

        TaskServiceException exception = assertThrows(TaskServiceException.class, () -> commentService.viewAllCommentsForTask(1L));
        assertEquals("Task not found", exception.getMessage());
    }
}
