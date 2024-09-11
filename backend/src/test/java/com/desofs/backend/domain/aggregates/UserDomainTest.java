package com.desofs.backend.domain.aggregates;

import com.desofs.backend.domain.enums.Authority;
import com.desofs.backend.domain.valueobjects.*;
import com.desofs.backend.dtos.UserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserDomainTest {

    @Test
    @DisplayName("Test constructor with valid UserDto")
    void testConstructorValidUserDto() {
        UserDto userDto = new UserDto(
                UUID.randomUUID().toString(),
                "John Doe One",
                "john.doe1@example.com",
                "Abcdef123!@#1",
                "910123123",
                Authority.CUSTOMER,
                true);

        UserDomain user = new UserDomain(userDto);
        assertNotNull(user);
        assertEquals(userDto.getId(), user.getId().value());
        assertEquals(userDto.getName(), user.getName().value());
        assertEquals(userDto.getEmail(), user.getEmail().value());
        assertEquals(userDto.getPassword(), user.getPassword().value());
        assertTrue(user.isBanned());
    }

    @Test
    @DisplayName("Test banUser method when user is not banned")
    void testBanUserNotBanned() {
        Id id = Id.create(UUID.randomUUID().toString());
        Name name = Name.create("John Doe Two");
        Email email = Email.create("john.doe2@example.com");
        Password password = Password.create("Abcdef123!@2");
        PhoneNumber phoneNumber = PhoneNumber.create("910123123");
        UserDomain user = new UserDomain(id, name, email, password, phoneNumber, Authority.CUSTOMER, false);
        assertTrue(user.banUser());
        assertTrue(user.isBanned());
    }

    @Test
    @DisplayName("Test banUser method when user is already banned")
    void testBanUserAlreadyBanned() {
        Id id = Id.create(UUID.randomUUID().toString());
        Name name = Name.create("John Doe Three");
        Email email = Email.create("john.doe3@example.com");
        Password password = Password.create("Abcdef123!@#3");
        PhoneNumber phoneNumber = PhoneNumber.create("911123123");
        UserDomain user = new UserDomain(id, name, email, password, phoneNumber, Authority.CUSTOMER, false);
        user.banUser();
        assertFalse(user.banUser());
        assertTrue(user.isBanned());
    }
}