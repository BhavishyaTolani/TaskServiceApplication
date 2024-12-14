package com.example.TaskApplication.DTO.responsePojos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private long id;
    private String name;
    private String emailId;
    private String userResponse;
}
