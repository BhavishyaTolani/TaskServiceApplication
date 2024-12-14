package com.example.TaskApplication.controllers;

import com.example.TaskApplication.DTO.responsePojos.UserResponse;
import com.example.TaskApplication.DTO.requestPojos.UserRequest;
import com.example.TaskApplication.exceptions.TaskServiceException;
import com.example.TaskApplication.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/loginId/{id}/password/{password}")
    UserResponse login(@PathVariable long id, @PathVariable String password) {
        try {
            return userService.login(id, password);
        }
        catch (TaskServiceException e) {
            throw new TaskServiceException("Unable to fetch user due to : " + e.getMessage());
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to fetch user due to : " + e.getMessage());
        }
    }

    @PostMapping
    UserResponse signUp(@RequestBody UserRequest userRequest) {
        try {
            return userService.signUp(userRequest);
        }
        catch (TaskServiceException e) {
            throw new TaskServiceException("Unable to sign up due to : " + e.getMessage());
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to sign up due to : " + e.getMessage());
        }
    }
}
