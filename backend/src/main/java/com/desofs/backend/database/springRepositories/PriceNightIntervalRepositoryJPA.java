package com.desofs.backend.database.springRepositories;

import com.desofs.backend.database.models.PriceNightIntervalDB;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PriceNightIntervalRepositoryJPA extends CrudRepository<PriceNightIntervalDB, String> {

    @Query("SELECT pni FROM price_night_interval pni WHERE pni.rentalPropertyId=:rentalPropertyId")
    List<PriceNightIntervalDB> findByRentalPropertyId(String rentalPropertyId);
}
