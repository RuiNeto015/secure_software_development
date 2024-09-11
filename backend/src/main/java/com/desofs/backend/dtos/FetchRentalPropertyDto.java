package com.desofs.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class FetchRentalPropertyDto {

    private final String id;

    private final String propertyOwner;

    private final String propertyName;

    private final LocationDto location;

    private final int maxGuests;

    private final int numBedrooms;

    private final int numBathrooms;

    private final String propertyDescription;

    private final BigDecimal amount;

    private final List<PriceNightIntervalDto> priceNightIntervalList;

    private final List<FetchBookingDto> bookingList;

    private final boolean isActive;
}