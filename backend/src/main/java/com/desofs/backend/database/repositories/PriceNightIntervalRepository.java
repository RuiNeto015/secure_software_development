package com.desofs.backend.database.repositories;

import com.desofs.backend.database.mappers.PriceNightIntervalMapper;
import com.desofs.backend.database.models.PriceNightIntervalDB;
import com.desofs.backend.database.springRepositories.PriceNightIntervalRepositoryJPA;
import com.desofs.backend.domain.valueobjects.PriceNightInterval;
import com.desofs.backend.exceptions.DatabaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PriceNightIntervalRepository {

    private final PriceNightIntervalRepositoryJPA priceNightIntervalJPA;

    private final PriceNightIntervalMapper mapper;

    public void create(PriceNightInterval priceNightInterval) throws DatabaseException {
        PriceNightIntervalDB priceNightIntervalDB =
                this.priceNightIntervalJPA.findById(priceNightInterval.getId().value()).orElse(null);

        if (priceNightIntervalDB != null) {
            throw new DatabaseException("Duplicated ID violation.");
        }

        this.priceNightIntervalJPA.save(this.mapper.domainToDb(priceNightInterval));
    }
}

