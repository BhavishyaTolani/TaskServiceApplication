package com.example.TaskApplication.services;

import com.example.TaskApplication.DTO.requestPojos.CommentRequest;
import com.example.TaskApplication.DTO.responsePojos.CommentResponse;
import com.example.TaskApplication.DTO.responsePojos.TaskResponse;
import com.example.TaskApplication.entities.Comment;
import com.example.TaskApplication.exceptions.TaskServiceException;
import com.example.TaskApplication.repositories.CommentRepository;
import com.example.TaskApplication.utils.Constants;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Data
public class CommentService {

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private CommentRepository commentRepository;

    public CommentResponse addComment(CommentRequest commentRequest) throws TaskServiceException {
        if(!Objects.equals(ApplicationContext.getCurrentUser(), commentRequest.getUserId()))
            throw new TaskServiceException("Invalid user for the add comment call. Logged in user id does not match with the user id used in comment");
        TaskResponse task = taskService.getTask(commentRequest.getTaskId());
        Comment comment = commentRepository.save(new Comment(task.getId(),
                commentRequest.getComment(),
                commentRequest.getUserId()));
        CommentResponse commentResponse = CommentResponse.builder()
                .commentId(comment.getId())
                .taskId(comment.getTask().getId())
                .userId(comment.getUser().getId())
                .commentResponse(Constants.COMMENT_ADDED_RESPONSE_MESSAGE)
                .build();
        return commentResponse;
    }

    public CommentResponse viewComment(long id) throws TaskServiceException {
        Optional<Comment> comment = commentRepository.findById(id);
        if(comment.isEmpty())
            throw new TaskServiceException("Comment does not exist");
        return CommentResponse.builder()
                .comment(comment.get().getContent())
                .taskId(comment.get().getTask().getId())
                .userId(comment.get().getUser().getId())
                .commentResponse(Constants.GET_COMMENT_SUCCESS_MESSAGE)
                .build();
    }

    public List<CommentResponse> viewAllCommentsForTask(long taskId) throws TaskServiceException {
        TaskResponse task = taskService.getTask(taskId);
        List<Comment> comments = commentRepository.findAllByTaskId(task.getId());
        if(comments.isEmpty()) {
            return List.of(CommentResponse.builder().commentResponse(String.format(Constants.NO_COMMENTS_EXISTS, taskId)).build());
        }
        return comments.stream()
                .sorted(Comparator.comparing(Comment::getCreatedDate))
                .map(comment -> CommentResponse.builder().commentId(comment.getId()).userId(comment.getUser().getId()).comment(comment.getContent()).taskId(comment.getTask().getId()).build())
                .collect(Collectors.toUnmodifiableList());
    }
}
