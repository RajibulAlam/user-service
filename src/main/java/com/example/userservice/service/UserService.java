package com.example.userservice.service;

import com.example.userservice.dto.UserDTO;
import com.example.userservice.entity.UserEntity;
import com.example.userservice.process.FileReaderFactory;
import com.example.userservice.process.FileReaderStrategy;
import com.example.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void saveUsers(MultipartFile file, String header) {
        validateFile(file);
        FileReaderStrategy fileReader = FileReaderFactory.getFileReader(header);
        try {
            List<UserDTO> users = fileReader.readFile(file);
            for (UserDTO user : users) {
                userRepository.save(new UserEntity(user.name(), user.email()));
            }
        } catch (IOException e) {
            // TODO: create a custom exception and also add a controller advisor to handle it
            throw new RuntimeException("Failed to process file", e);
        }
    }

    public List<UserDTO> getAllUsers() {
        List<UserEntity> users = userRepository.findAll();
        return users.stream()
                .map(user -> new UserDTO(user.getName(), user.getEmail()))
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
