package com.desofs.backend.database.mappers;

import com.desofs.backend.database.models.RentalPropertyDB;
import com.desofs.backend.domain.aggregates.BookingDomain;
import com.desofs.backend.domain.aggregates.RentalPropertyDomain;
import com.desofs.backend.domain.valueobjects.*;
import com.desofs.backend.dtos.FetchRentalPropertyDto;
import com.desofs.backend.dtos.IntervalTimeDto;
import com.desofs.backend.dtos.LocationDto;
import com.desofs.backend.dtos.PriceNightIntervalDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class RentalPropertyMapper {

    private final BookingMapper bookingMapper = new BookingMapper();

    public RentalPropertyDomain toDomainObject(RentalPropertyDB rentalProperty,
                                               List<PriceNightInterval> priceNightIntervals,
                                               List<BookingDomain> bookings) {
        return new RentalPropertyDomain(
                Id.create(rentalProperty.getId()),
                Id.create(rentalProperty.getPropertyOwnerId()), PropertyName.create(rentalProperty.getPropertyName()),
                Location.create(rentalProperty.getLat(), rentalProperty.getLon()),
                PositiveInteger.create(rentalProperty.getMaxGuests()),
                PositiveInteger.create(rentalProperty.getNumBedrooms()),
                PositiveInteger.create(rentalProperty.getNumBathrooms()),
                PropertyDescription.create(rentalProperty.getPropertyDescription()),
                MoneyAmount.create(new BigDecimal(rentalProperty.getPriceNightDefault())),
                priceNightIntervals,
                bookings,
                rentalProperty.isActive()
        );
    }

    public RentalPropertyDB toDatabaseObject(RentalPropertyDomain rentalProperty) {
        return new RentalPropertyDB(rentalProperty.getId().value(), rentalProperty.getDefaultNightPrice().value().toString(),
                rentalProperty.getPropertyOwner().value(), rentalProperty.getPropertyName().value(),
                rentalProperty.getLocation().getLat(), rentalProperty.getLocation().getLon(),
                rentalProperty.getMaxGuests().value(), rentalProperty.getNumBedrooms().value(),
                rentalProperty.getNumBathrooms().value(), rentalProperty.getPropertyDescription().value(),
                rentalProperty.getIsActive());
    }

    public FetchRentalPropertyDto domainToDto(RentalPropertyDomain rentalProperty) {
        return new FetchRentalPropertyDto(
                rentalProperty.getId().value(),
                rentalProperty.getPropertyOwner().value(),
                rentalProperty.getPropertyName().value(),
                new LocationDto(rentalProperty.getLocation().getLat(), rentalProperty.getLocation().getLon()),
                rentalProperty.getMaxGuests().value(),
                rentalProperty.getNumBedrooms().value(),
                rentalProperty.getNumBathrooms().value(),
                rentalProperty.getPropertyDescription().value(),
                rentalProperty.getDefaultNightPrice().value(),
                rentalProperty.getPriceNightIntervalList().stream().map(priceNightInterval -> {
                    IntervalTimeDto tempIntervalTime = new IntervalTimeDto(priceNightInterval.getInterval().getFrom(),
                            priceNightInterval.getInterval().getTo());
                    return new PriceNightIntervalDto(rentalProperty.getId().value(),
                            priceNightInterval.getPrice().value(), tempIntervalTime);
                }).toList(),
                rentalProperty.getBookingList().stream().map(booking -> bookingMapper.domainToDto(booking, rentalProperty.getId().value())).toList(),
                rentalProperty.getIsActive());
    }
}
