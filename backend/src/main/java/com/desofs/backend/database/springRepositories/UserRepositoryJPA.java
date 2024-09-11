package com.desofs.backend.database.springRepositories;

import com.desofs.backend.database.models.UserDB;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepositoryJPA extends CrudRepository<UserDB, String> {

    @Query("SELECT u FROM user u WHERE u.email=:email")
    Optional<UserDB> findByEmail(String email);
}
