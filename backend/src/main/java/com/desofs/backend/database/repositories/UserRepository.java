package com.desofs.backend.database.repositories;

import com.desofs.backend.exceptions.DatabaseException;
import com.desofs.backend.database.mappers.UserMapper;
import com.desofs.backend.database.models.UserDB;
import com.desofs.backend.database.springRepositories.UserRepositoryJPA;
import com.desofs.backend.domain.aggregates.UserDomain;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("UserRepositoryCapsule")
public class UserRepository {

    private final UserRepositoryJPA userRepository;
    private final UserMapper mapper;

    public UserRepository(UserRepositoryJPA userRepository, UserMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    public void create(UserDomain user) throws DatabaseException {
        Optional<UserDB> userById = this.userRepository.findById(user.getId().value());

        if (userById.isPresent()) {
            throw new DatabaseException("Duplicated ID violation.");
        }

        Optional<UserDB> userByEmail = this.userRepository.findByEmail(user.getEmail().value());

        if (userByEmail.isPresent()) {
            throw new DatabaseException("Duplicated email violation.");
        }

        this.userRepository.save(this.mapper.toDatabaseObject(user));
    }

    public UserDomain findByEmail(String email) {
        try {
            Optional<UserDB> user = this.userRepository.findByEmail(email);
            if (user.isPresent()) {
                return user.map(this.mapper::toDomainObject).orElse(null);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public UserDomain findById(String id) {
        try {
            Optional<UserDB> user = this.userRepository.findById(id);
            if (user.isPresent()) {
                return user.map(this.mapper::toDomainObject).orElse(null);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public void update(UserDomain user) {
        this.userRepository.save(this.mapper.toDatabaseObject(user));
    }
}
