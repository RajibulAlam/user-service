package com.example.userservice.dto;

public record SpecialUser(String name, String email, String role) implements User {
    @Override
    public String getUser() {
        return name + " " + role;
    }
}
