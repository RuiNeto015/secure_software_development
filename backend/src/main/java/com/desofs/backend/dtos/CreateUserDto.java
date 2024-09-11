package com.desofs.backend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CreateUserDto {

    @NotBlank(message = "Name is mandatory")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private final String name;

    @NotNull(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private final String email;

    @NotNull(message = "Password is mandatory")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private final String password;

    @NotBlank(message = "Phone number is mandatory")
    private final String phoneNumber;

    @NotBlank(message = "Role is mandatory")
    private final String role;
}