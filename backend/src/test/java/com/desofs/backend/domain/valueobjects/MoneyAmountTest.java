package com.desofs.backend.domain.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyAmountTest {

    @Test
    @DisplayName("Test create method with valid money amount")
    void testCreateValidMoneyAmount() {
        MoneyAmount moneyAmount = MoneyAmount.create(BigDecimal.valueOf(100.50));
        assertNotNull(moneyAmount);
        assertEquals(BigDecimal.valueOf(100.50), moneyAmount.getValue());
    }

    @Test
    @DisplayName("Test create method with negative money amount")
    void testCreateNegativeMoneyAmount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> MoneyAmount.create(BigDecimal.valueOf(-100.50)));
        assertEquals("MoneyAmount must be positive.", exception.getMessage());
    }
}