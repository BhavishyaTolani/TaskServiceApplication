package com.example.TaskApplication.services;

import com.example.TaskApplication.DTO.responsePojos.UserResponse;
import com.example.TaskApplication.DTO.requestPojos.UserRequest;
import com.example.TaskApplication.entities.User;
import com.example.TaskApplication.exceptions.TaskServiceException;
import com.example.TaskApplication.repositories.UserRepository;
import com.example.TaskApplication.utils.Constants;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Optional<User> getUser(long id) {
        Optional<User> user = userRepository.findById(id);
        return user;
    }


    public UserResponse login(long id, String password) throws TaskServiceException {
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()) {
            User currUser = user.get();
            if(currUser.getPassword().equals(password)) {
                ApplicationContext.setCurrentUser(currUser.getId());
                ApplicationContext.setCurrentUserEmail(currUser.getEmail());
                return UserResponse.builder()
                        .emailId(currUser.getEmail())
                        .name(currUser.getName())
                        .id(currUser.getId())
                        .userResponse(Constants.SUCCESSFUL_USER_SIGN_IN)
                        .build();
            }
            else
                throw new TaskServiceException("Password does not match");
        }
        else
            throw new TaskServiceException("User does not exist");

    }

    public UserResponse signUp(UserRequest userRequest) throws TaskServiceException {
        Optional<User> user = userRepository.findByEmail(userRequest.getEmail());
        if (user.isPresent()) {
            throw new TaskServiceException("User Already exists please login with login credentails");
        }

        User newUser = userRepository.save(
                User.builder()
                        .email(userRequest.getEmail())
                        .password(userRequest.getPassword())
                        .name(userRequest.getName())
                        .build());
        return UserResponse.builder()
                .emailId(newUser.getEmail())
                .name(newUser.getName())
                .id(newUser.getId())
                .userResponse(Constants.SUCCESSFUL_USER_SIGN_UP)
                .build();
    }
}
