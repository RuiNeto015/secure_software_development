package com.desofs.backend.database.springRepositories;

import com.desofs.backend.database.models.ReviewDB;
import org.springframework.data.repository.CrudRepository;

public interface ReviewRepositoryJPA extends CrudRepository<ReviewDB, String> {

    ReviewDB findByBookingId(String bookingId);
}
