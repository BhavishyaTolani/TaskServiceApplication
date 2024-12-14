package com.example.TaskApplication.controllers;

import com.example.TaskApplication.DTO.requestPojos.CommentRequest;
import com.example.TaskApplication.DTO.responsePojos.CommentResponse;
import com.example.TaskApplication.DTO.requestPojos.TaskRequest;
import com.example.TaskApplication.DTO.responsePojos.TaskResponse;
import com.example.TaskApplication.exceptions.TaskServiceException;
import com.example.TaskApplication.services.CommentService;
import com.example.TaskApplication.services.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;

    private final CommentService commentService;

    @GetMapping("/taskId/{taskId}")
    TaskResponse readTask(@PathVariable long taskId) {
        try {
            return taskService.getTask(taskId);
        }
        catch (TaskServiceException e) {
            throw new TaskServiceException("Unable to fetch task due to : %s", e);
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to fetch task due to : %s" + e.getMessage());
        }
    }

    @PostMapping
    TaskResponse createTask(@RequestBody TaskRequest taskRequest) {
        try{
            return taskService.createTask(taskRequest);
        }
        catch (TaskServiceException e) {
            throw new TaskServiceException("Unable to create task due to : " + e.getMessage());
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to create task due to : " + e.getMessage());
        }
    }

    @PostMapping("/comment")
    CommentResponse addComment(@RequestBody CommentRequest commentRequest) {
        try {
            return commentService.addComment(commentRequest);
        }
        catch (TaskServiceException e) {
            throw new TaskServiceException("Unable to add comment due to : " + e.getMessage());
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to add comment due to : " + e.getMessage());
        }
    }

    @GetMapping("/comments/{commentId}")
    CommentResponse viewComment(@PathVariable long commentId) {
        try {
            return commentService.viewComment(commentId);
        }
        catch (TaskServiceException e) {
            throw new TaskServiceException("Unable to fetch comment due to : " + e.getMessage());
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to fetch comment due to : " + e.getMessage());
        }
    }

    @GetMapping("/taskId/{taskId}/comments")
    List<CommentResponse> viewAllComments(@PathVariable long taskId) {
        try {
            return commentService.viewAllCommentsForTask(taskId);
        }
        catch (TaskServiceException e) {
            throw new TaskServiceException("Unable to fetch all comments for a task : " + e.getMessage());
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to fetch all comments for a task : " + e.getMessage());
        }
    }
}
