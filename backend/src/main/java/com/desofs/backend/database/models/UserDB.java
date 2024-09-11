package com.desofs.backend.database.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;

import lombok.Getter;

@Entity(name = "user")
@Getter
public class UserDB {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private boolean isBanned;

    public UserDB() {
    }

    public UserDB(final String id, final String name, final String email, final String password,
                  final String phoneNumber, final String role, final boolean isBanned) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.isBanned = isBanned;
    }
}