package com.desofs.backend.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CreatePaymentDto {

    @NotNull(message = "Money amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Money amount must be greater than zero")
    private final BigDecimal moneyAmount;

    @NotNull(message = "Credit card number is required")
    private final String creditCardNumber;

    @NotNull(message = "Card verification code is required")
    @Size(min = 3, max = 4, message = "Card verification code must be between 3 and 4 digits")
    private final String cardVerificationCode;

    @NotNull(message = "Expiration date is required")
    @Future(message = "Expiration date must be in the future")
    private final LocalDateTime expirationDate;

    @NotNull(message = "Email is required")
    @Email(message = "Email should be valid")
    private final String email;

    @NotNull(message = "Person name is required")
    private final String personName;

    @NotNull(message = "Creation date is required")
    private final LocalDateTime createdAt;
}
