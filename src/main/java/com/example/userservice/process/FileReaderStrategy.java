package com.example.userservice.process;

import com.example.userservice.dto.UserDTO;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface FileReaderStrategy {
    List<UserDTO> readFile(MultipartFile file) throws IOException;
}
