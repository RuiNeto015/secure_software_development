package com.desofs.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@AllArgsConstructor
@Getter
public class LoginRequestDto {

    @NotNull(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotNull(message = "Password is required")
    private String password;

    @NotNull(message = "Phone number is required")
    private String phoneNumber;

    @NotNull(message = "Code is required")
    @Size(min = 6, max = 6, message = "Code must be 6 characters")
    private String code;
}
