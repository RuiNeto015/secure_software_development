package com.desofs.backend.domain.entities;

import com.desofs.backend.domain.valueobjects.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PaymentEntityTest {

    @Test
    @DisplayName("Test constructor with valid parameters")
    void testConstructorValidParameters() {
        Id id = Id.create(UUID.randomUUID().toString());
        Id bookingId = Id.create(UUID.randomUUID().toString());
        MoneyAmount moneyAmount = MoneyAmount.create(BigDecimal.valueOf(100));
        CreditCardNumber creditCardNumber = CreditCardNumber.create("4111111111111111");
        CardVerificationCode cardVerificationCode = CardVerificationCode.create("123");
        LocalDateTime expirationDate = LocalDateTime.of(2025, 12, 31, 23, 59);
        Email email = Email.create("john.doe@example.com");
        Name name = Name.create("John Doe");
        LocalDateTime createdAt = LocalDateTime.now();

        PaymentEntity paymentEntity = new PaymentEntity(id, bookingId, moneyAmount, creditCardNumber,
                cardVerificationCode, expirationDate, email, name, createdAt);

        assertNotNull(paymentEntity);
        assertEquals(id.value(), paymentEntity.getId().value());
        assertEquals(moneyAmount.value(), paymentEntity.getMoneyAmount().value());
        assertEquals(creditCardNumber.value(), paymentEntity.getCreditCardNumber().value());
        assertEquals(cardVerificationCode.value(), paymentEntity.getCardVerificationCode().value());
        assertEquals(expirationDate, paymentEntity.getExpirationDate());
        assertEquals(email.value(), paymentEntity.getEmail().value());
        assertEquals(name.value(), paymentEntity.getName().value());
        assertEquals(createdAt, paymentEntity.getCreatedAt());
    }
}
