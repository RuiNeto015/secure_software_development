package com.desofs.backend.database.mappers;

import com.desofs.backend.database.models.PriceNightIntervalDB;
import com.desofs.backend.domain.valueobjects.Id;
import com.desofs.backend.domain.valueobjects.IntervalTime;
import com.desofs.backend.domain.valueobjects.MoneyAmount;
import com.desofs.backend.domain.valueobjects.PriceNightInterval;
import org.springframework.stereotype.Component;

@Component
public class PriceNightIntervalMapper {

    public PriceNightIntervalDB domainToDb(PriceNightInterval priceNightInterval) {
        return new PriceNightIntervalDB(
                priceNightInterval.getId().value(),
                priceNightInterval.getRentalPropertyId().value(),
                priceNightInterval.getPrice().value(),
                priceNightInterval.getInterval().getFrom(),
                priceNightInterval.getInterval().getTo());
    }

    public PriceNightInterval dbToDomain(PriceNightIntervalDB priceNightInterval) {
        return new PriceNightInterval(
                Id.create(priceNightInterval.getId()),
                Id.create(priceNightInterval.getRentalPropertyId()),
                MoneyAmount.create(priceNightInterval.getPrice()),
                IntervalTime.create(priceNightInterval.getFromDate(), priceNightInterval.getToDate()));
    }
}
