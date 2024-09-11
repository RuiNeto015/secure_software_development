package com.desofs.backend.domain.aggregates;

import com.desofs.backend.domain.valueobjects.*;
import com.desofs.backend.dtos.UserDto;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

import static org.apache.commons.lang3.Validate.notNull;

@ToString
public class UserDomain {

    private final Id id;
    private final Name name;
    private final Email email;
    private final Password password;
    @Getter
    private String role;
    private PhoneNumber phoneNumber;
    @Getter
    private boolean isBanned;

    public UserDomain(Id id, Name name, Email email, Password password, PhoneNumber phoneNumber, String role, boolean isBanned) {
        notNull(id, "Id must not be null.");
        notNull(name, "Name must not be null.");
        notNull(email, "Email must not be null.");
        notNull(password, "Password must not be null.");
        notNull(phoneNumber, "Phone number must not be null.");
        notNull(role, "Role must not be null.");

        this.id = id.copy();
        this.name = name.copy();
        this.email = email.copy();
        this.password = password.copy();
        this.phoneNumber = phoneNumber.copy();
        this.role = role;
        this.isBanned = isBanned;
    }

    // Used to create a user
    public UserDomain(Name name, Email email, Password password, PhoneNumber phoneNumber, String role) {
        notNull(name, "Name must not be null.");
        notNull(email, "Email must not be null.");
        notNull(password, "Password must not be null.");
        notNull(phoneNumber, "Phone Number must not be null.");
        notNull(role, "Role must not be null.");

        this.id = Id.create(UUID.randomUUID().toString());
        this.name = name.copy();
        this.email = email.copy();
        this.password = password.copy();
        this.phoneNumber = phoneNumber.copy();
        this.role = role;
        this.isBanned = false;
    }

    // Used for tests
    public UserDomain(UserDto userDto) {
        this(Id.create(userDto.getId()),
                Name.create(userDto.getName()),
                Email.create(userDto.getEmail()),
                Password.create(userDto.getPassword()),
                PhoneNumber.create(userDto.getPhoneNumber()),
                userDto.getRole(),
                userDto.isBanned());
    }

    public boolean banUser() {
        if (this.isBanned) {
            return false;
        } else {
            this.isBanned = true;
            return true;
        }
    }

    public Id getId() {
        return this.id.copy();
    }

    public Name getName() {
        return this.name.copy();
    }

    public Email getEmail() {
        return this.email.copy();
    }

    public Password getPassword() {
        return this.password.copy();
    }

    public PhoneNumber getPhoneNumber() {
        return this.phoneNumber.copy();
    }
}