package com.example.userservice.service;

import com.example.userservice.dto.UserDTO;
import com.example.userservice.entity.UserEntity;
import com.example.userservice.process.FileReaderFactory;
import com.example.userservice.process.FileReaderStrategy;
import com.example.userservice.repository.UserRepository;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileReaderStrategy fileReaderStrategy;

    @InjectMocks
    private UserService userService;

    @Mock
    private Validator validator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveUsers() throws IOException {
        MultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "firstName,lastName,email,age,userType\nFirst,Last,firstlast@example.com,20,Admin".getBytes());
        String headerFormat = "Default";

        List<UserDTO> userDTOs = Arrays.asList(new UserDTO("First", "Last", "firstlast@example.com", 20, "Admin"));

        // Mock the static method
        try (MockedStatic<FileReaderFactory> mockedFactory = mockStatic(FileReaderFactory.class)) {
            mockedFactory.when(() -> FileReaderFactory.getFileReader(headerFormat)).thenReturn(fileReaderStrategy);

            // Ensure readFile method of fileReaderStrategy is mocked
            when(fileReaderStrategy.readFile(file)).thenReturn(userDTOs);
            when(validator.validate(any(UserDTO.class))).thenReturn(Set.of());

            userService.saveUsers(file, headerFormat);

            mockedFactory.verify(() -> FileReaderFactory.getFileReader(headerFormat), times(1));
            verify(fileReaderStrategy, times(1)).readFile(file);
            verify(userRepository, times(1)).save(any(UserEntity.class));
        }
    }

    @Test
    void testGetAllUsers() {
        List<UserEntity> userEntities = Arrays.asList(new UserEntity("First", "Last", "firstlast@example.com", 20, "Admin"));
        when(userRepository.findAll()).thenReturn(userEntities);

        List<UserDTO> expectedUsers = userEntities.stream()
                .map(user -> new UserDTO(user.getFirstName(), user.getLastName(), user.getEmail(), user.getAge(), user.getUserType()))
                .collect(Collectors.toList());

        List<UserDTO> users = userService.getAllUsers();

        assertEquals(expectedUsers, users);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testValidateFile_EmptyFile() {
        MultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", new byte[0]);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.saveUsers(file, "Default"));

        assertEquals("File is empty", exception.getMessage());
    }

    @Test
    void testValidateFile_InvalidFileType() {
        MultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "firstName,lastName,email,age,userType\nFirst,Last,firstlast@example.com,20,Admin".getBytes());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.saveUsers(file, "Default"));

        assertEquals("Invalid file type. Only CSV files are allowed.", exception.getMessage());
    }

    @Test
    void testValidateFile_FileTooLarge() {
        byte[] largeFileContent = new byte[6 * 1024 * 1024]; // 6MB
        MultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", largeFileContent);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.saveUsers(file, "Default"));

        assertEquals("File size exceeds the limit of 5MB.", exception.getMessage());
    }
}
