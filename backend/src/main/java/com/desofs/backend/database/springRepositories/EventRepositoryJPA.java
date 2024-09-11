
package com.desofs.backend.database.springRepositories;

import com.desofs.backend.database.models.EventDB;
import com.desofs.backend.database.models.RentalPropertyDB;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EventRepositoryJPA extends CrudRepository<EventDB, String> {

    List<EventDB> findAllByBookingId(String id);

}
