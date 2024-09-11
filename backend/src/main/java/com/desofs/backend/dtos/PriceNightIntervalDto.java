package com.desofs.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class PriceNightIntervalDto {

    @NotNull(message = "Rental property ID is required")
    private final String rentalPropertyId;

    @NotNull(message = "Price is required")
    private final BigDecimal price;

    @NotNull(message = "Interval is required")
    private final IntervalTimeDto interval;
}
