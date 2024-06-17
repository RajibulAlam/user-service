package com.example.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SpecialUser (       @NotBlank
                                  @Size(min = 2, max = 50)
                                  String firstName,

                                  @NotBlank
                                  @Size(min = 2, max = 50)
                                  String lastName,

                                  @NotBlank
                                  @Email
                                  String email,

                                  @Min(18)
                                  int age,

                                  @NotBlank
                                  String userType,

                                  @NotBlank
                                  String additionalInfo
) implements User {
    @Override
    public String getUser() {
        return firstName+" "+lastName+" "+additionalInfo;
    }
}
