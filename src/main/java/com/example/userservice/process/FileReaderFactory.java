package com.example.userservice.process;

public class FileReaderFactory {

    public static FileReaderStrategy getFileReader(String headerFormat) {
        if (headerFormat.equals("Default")) {
            return new DefaultFileReader();
        } else {
            return new CustomHeaderFileReader();
        }
    }
}
