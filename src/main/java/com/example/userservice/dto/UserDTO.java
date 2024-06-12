package com.example.userservice.dto;

public record UserDTO (String name, String email) implements User {
    @Override
    public String getUser() {
        return name;
    }
}
