package com.desofs.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDto {

    private String id;

    private String name;

    private String email;

    private String password;

    private String phoneNumber;

    private String role;

    private boolean isBanned;
}
