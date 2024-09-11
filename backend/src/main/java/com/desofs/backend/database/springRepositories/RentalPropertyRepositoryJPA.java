package com.desofs.backend.database.springRepositories;

import com.desofs.backend.database.models.RentalPropertyDB;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RentalPropertyRepositoryJPA extends CrudRepository<RentalPropertyDB, String> {

    public List<RentalPropertyDB> findAllByPropertyOwnerId(String id);

}
