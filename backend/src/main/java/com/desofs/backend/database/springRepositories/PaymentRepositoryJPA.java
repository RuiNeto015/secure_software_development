package com.desofs.backend.database.springRepositories;

import com.desofs.backend.database.models.PaymentDB;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PaymentRepositoryJPA extends CrudRepository<PaymentDB, String> {

    Optional<PaymentDB> findByBookingId(String bookingId);
}
