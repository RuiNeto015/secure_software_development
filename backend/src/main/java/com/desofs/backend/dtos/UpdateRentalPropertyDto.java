package com.desofs.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class UpdateRentalPropertyDto {
    @NotNull(message = "Property name is required")
    private final String propertyName;

    private LocationDto location;

    @NotNull(message = "Maximum guests must be specified")
    private final int maxGuests;

    @NotNull(message = "Number of bedrooms must be specified")
    private final int numBedrooms;

    @NotNull(message = "Number of bathrooms must be specified")
    private final int numBathrooms;

    @NotNull(message = "Property description is required")
    private final String propertyDescription;

    @NotNull(message = "Amount is required")
    private final BigDecimal amount;

    @NotNull(message = "Price night interval list is required")
    private final List<PriceNightIntervalDto> priceNightIntervalList;
}
