package com.desofs.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FetchUserDto {

    private final String id;

    private final String name;

    private final String email;

    private final String phoneNumber;

    private final String role;

    private final boolean isBanned;
}
