package com.desofs.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PaymentDto {

    private final String id;

    private final String bookingId;

    private final BigDecimal moneyAmount;

    private final String creditCardNumber;

    private final String cardVerificationCode;

    private final LocalDateTime expirationDate;

    private final String email;

    private final String personName;

    private final LocalDateTime createdAt;
}
