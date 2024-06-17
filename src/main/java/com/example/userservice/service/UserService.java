package com.example.userservice.service;

import com.example.userservice.dto.UserDTO;
import com.example.userservice.entity.UserEntity;
import com.example.userservice.process.FileReaderFactory;
import com.example.userservice.process.FileReaderStrategy;
import com.example.userservice.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Validator;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final Validator validator;

    public UserService(UserRepository userRepository, Validator validator) {
        this.userRepository = userRepository;
        this.validator = validator;
    }

    public void saveUsers(MultipartFile file, String headerFormat) {
        validateFile(file);
        FileReaderStrategy fileReader = FileReaderFactory.getFileReader(headerFormat);
        try {
            List<@Valid UserDTO> users = fileReader.readFile(file);
            for (UserDTO user : users) {
                var violations = validator.validate(user);
                if (!violations.isEmpty()) {
                    throw new RuntimeException("Validation errors: " + violations);
                }
                userRepository.save(new UserEntity(user.firstName(), user.lastName(), user.email(), user.age(), user.userType()));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to process file", e);
        }
    }

    public List<UserDTO> getAllUsers() {
        List<UserEntity> users = userRepository.findAll();
        return users.stream()
                .map(user -> new UserDTO(user.getFirstName(), user.getLastName(), user.getEmail(), user.getAge(), user.getUserType()))
                .collect(Collectors.toList());
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        if (!file.getOriginalFilename().endsWith(".csv")) {
            throw new RuntimeException("Invalid file type. Only CSV files are allowed.");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("File size exceeds the limit of 5MB.");
        }
    }
}
