package com.example.TaskApplication.services;

import com.example.TaskApplication.DTO.requestPojos.UserRequest;
import com.example.TaskApplication.DTO.responsePojos.UserResponse;
import com.example.TaskApplication.entities.User;
import com.example.TaskApplication.exceptions.TaskServiceException;
import com.example.TaskApplication.repositories.UserRepository;
import com.example.TaskApplication.utils.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUser_UserExists() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUser(userId);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUser_UserDoesNotExist() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUser(userId);

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testLogin_Success() throws TaskServiceException {
        long userId = 1L;
        String password = "password123";
        User mockUser = User.builder()
                .id(userId)
                .name("John Doe")
                .email("john.doe@example.com")
                .password(password)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        UserResponse userResponse = userService.login(userId, password);

        assertNotNull(userResponse);
        assertEquals(mockUser.getEmail(), userResponse.getEmailId());
        assertEquals(mockUser.getName(), userResponse.getName());
        assertEquals(mockUser.getId(), userResponse.getId());
        assertEquals(Constants.SUCCESSFUL_USER_SIGN_IN, userResponse.getUserResponse());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testLogin_UserDoesNotExist() {
        long userId = 1L;
        String password = "password123";
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        TaskServiceException exception = assertThrows(TaskServiceException.class, () -> userService.login(userId, password));
        assertEquals("User does not exist", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testLogin_PasswordMismatch() {
        long userId = 1L;
        String password = "wrongpassword";
        User mockUser = User.builder()
                .id(userId)
                .name("John Doe")
                .email("john.doe@example.com")
                .password("correctpassword")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        TaskServiceException exception = assertThrows(TaskServiceException.class, () -> userService.login(userId, password));
        assertEquals("Password does not match", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testSignUp_Success() throws TaskServiceException {
        UserRequest userRequest = UserRequest.builder()
                .email("john.doe@example.com")
                .name("John Doe")
                .password("password123")
                .build();

        User newUser = User.builder()
                .id(1L)
                .email(userRequest.getEmail())
                .name(userRequest.getName())
                .password(userRequest.getPassword())
                .build();

        when(userRepository.findByEmail(userRequest.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        UserResponse userResponse = userService.signUp(userRequest);

        assertNotNull(userResponse);
        assertEquals(userRequest.getEmail(), userResponse.getEmailId());
        assertEquals(userRequest.getName(), userResponse.getName());
        assertEquals(Constants.SUCCESSFUL_USER_SIGN_UP, userResponse.getUserResponse());
        verify(userRepository, times(1)).findByEmail(userRequest.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testSignUp_UserAlreadyExists() {
        UserRequest userRequest = UserRequest.builder()
                .email("john.doe@example.com")
                .name("John Doe")
                .password("password123")
                .build();

        User existingUser = User.builder()
                .id(1L)
                .email(userRequest.getEmail())
                .name(userRequest.getName())
                .password(userRequest.getPassword())
                .build();

        when(userRepository.findByEmail(userRequest.getEmail())).thenReturn(Optional.of(existingUser));

        TaskServiceException exception = assertThrows(TaskServiceException.class, () -> userService.signUp(userRequest));
        assertEquals("User Already exists please login with login credentails", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(userRequest.getEmail());
    }
}

