package com.example.userservice.process;

import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import com.example.userservice.dto.UserDTO;

public class DefaultFileReader implements FileReaderStrategy {

    @Override
    public List<UserDTO> readFile(MultipartFile file) throws IOException {
        List<UserDTO> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            //skip header
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                users.add(new UserDTO(data[0], data[1]));
            }
        }
        return users;
    }
}

