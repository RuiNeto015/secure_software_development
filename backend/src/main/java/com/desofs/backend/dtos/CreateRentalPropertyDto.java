package com.desofs.backend.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class CreateRentalPropertyDto {

    @NotNull(message = "Property name is required")
    @Size(min = 1, max = 100, message = "Property name length must be between 1 and 100 characters")
    private final String propertyName;

    @NotNull(message = "Location is required")
    private LocationDto location;

    @NotNull(message = "Maximum guests must be specified")
    @Min(value = 1, message = "At least 1 guest is required")
    private final int maxGuests;

    @NotNull(message = "Number of bedrooms must be specified")
    @Min(value = 1, message = "At least 1 bedroom is required")
    private final int numBedrooms;

    @NotNull(message = "Number of bathrooms must be specified")
    @Min(value = 1, message = "At least 1 bathroom is required")
    private final int numBathrooms;

    @NotNull(message = "Property description is required")
    private final String propertyDescription;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero")
    private final BigDecimal amount;

    @NotNull(message = "Price night interval list is required")
    @NotEmpty(message = "Price night interval list must not be empty")
    private final List<PriceNightIntervalDto> priceNightIntervalList;
}