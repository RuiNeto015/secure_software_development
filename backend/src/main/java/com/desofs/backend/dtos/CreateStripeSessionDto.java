package com.desofs.backend.dtos;

public record CreateStripeSessionDto(String propertyId, IntervalTimeDto intervalTime, String successUrl) {
}
