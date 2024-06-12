package com.example.userservice.process;

import com.example.userservice.dto.UserDTO;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CustomHeaderFileReader implements FileReaderStrategy {


    public CustomHeaderFileReader() {

    }

    @Override
    public List<UserDTO> readFile(MultipartFile file) throws IOException {
        List<UserDTO> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {

            }
        }
        return users;
    }
}
