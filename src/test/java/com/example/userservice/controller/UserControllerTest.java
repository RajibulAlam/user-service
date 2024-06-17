package com.example.userservice.controller;

import com.example.userservice.dto.UserDTO;
import com.example.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadFile() {
        MultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "firstName,lastName,email,age,userType\nJohn,Doe,johndoe@example.com,30,Admin".getBytes());
        String headerFormat = "Default";

        doNothing().when(userService).saveUsers(file, headerFormat);

        ResponseEntity<?> response = userController.uploadFile(file, headerFormat);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService, times(1)).saveUsers(file, headerFormat);
    }

    @Test
    void testGetAllUsers() {
        List<UserDTO> expectedUsers = Arrays.asList(new UserDTO("John", "Doe", "johndoe@example.com", 30, "Admin"));
        when(userService.getAllUsers()).thenReturn(expectedUsers);

        List<UserDTO> response = userController.getAllUsers();

        assertEquals(expectedUsers, response);
        verify(userService, times(1)).getAllUsers();
    }
}
