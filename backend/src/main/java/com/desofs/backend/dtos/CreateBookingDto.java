package com.desofs.backend.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateBookingDto {

    @NotNull(message = "Property ID is required")
    private final String propertyId;

    @NotNull(message = "Interval time is required")
    private IntervalTimeDto intervalTime;
}
