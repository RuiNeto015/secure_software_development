package com.desofs.backend.domain.valueobjects;

import lombok.Getter;

import java.math.BigDecimal;

import static org.apache.commons.lang3.Validate.isTrue;

@Getter
public class MoneyAmount {

    private final BigDecimal value;

    private MoneyAmount(BigDecimal value) {
        this.value = value;
    }

    public static MoneyAmount create(BigDecimal value) {
        isTrue(value.compareTo(BigDecimal.valueOf(0)) >= 0,
                "MoneyAmount must be positive.");
        return new MoneyAmount(value);
    }

    public MoneyAmount copy() {
        return new MoneyAmount(value);
    }

    public BigDecimal value() {
        return BigDecimal.valueOf(value.longValue());
    }

}
