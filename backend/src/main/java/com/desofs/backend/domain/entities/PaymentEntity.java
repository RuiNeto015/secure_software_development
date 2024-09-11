package com.desofs.backend.domain.entities;

import com.desofs.backend.domain.valueobjects.*;
import com.desofs.backend.dtos.CreatePaymentDto;
import com.desofs.backend.dtos.PaymentDto;
import com.desofs.backend.utils.LocalDateTimeUtils;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.apache.commons.lang3.Validate.notNull;

public class PaymentEntity {

    private final Id id;
    private final Id bookingId;
    private final MoneyAmount moneyAmount;
    private final CreditCardNumber creditCardNumber;
    private final CardVerificationCode cardVerificationCode;
    private final LocalDateTime expirationDate;
    private final Email email;
    private final Name name;
    private final LocalDateTime createdAt;

    // Constructors ----------------------------------------------------------------------------------------------------

    public PaymentEntity(Id id, Id bookingId, MoneyAmount moneyAmount, CreditCardNumber creditCardNumber,
                         CardVerificationCode cardVerificationCode, LocalDateTime expirationDate,
                         Email email, Name personalName, LocalDateTime createdAt) {
        notNull(id, "Id must not be null.");
        notNull(bookingId, "BookingId must not be null.");
        notNull(moneyAmount, "Money amount must not be null.");
        notNull(creditCardNumber, "Credit card number must not be null.");
        notNull(cardVerificationCode, "Card verification code must not be null.");
        notNull(expirationDate, "Expiration date must not be null.");
        notNull(email, "Email must not be null.");
        notNull(personalName, "Personal name must not be null.");
        notNull(createdAt, "Creation date must not be null.");

        this.id = id.copy();
        this.bookingId = bookingId.copy();
        this.moneyAmount = moneyAmount.copy();
        this.creditCardNumber = creditCardNumber.copy();
        this.cardVerificationCode = cardVerificationCode.copy();
        this.expirationDate = expirationDate;
        this.email = email.copy();
        this.name = personalName.copy();
        this.createdAt = createdAt;
    }

    public PaymentEntity(CreatePaymentDto dto, String bookingId) {
        this(Id.create(UUID.randomUUID().toString()),
                Id.create(bookingId),
                MoneyAmount.create(dto.getMoneyAmount()),
                CreditCardNumber.create(dto.getCreditCardNumber()),
                CardVerificationCode.create(dto.getCardVerificationCode()),
                dto.getExpirationDate(),
                Email.create(dto.getEmail()),
                Name.create(dto.getPersonName()),
                dto.getCreatedAt());
    }

    public PaymentEntity copy() {
        return new PaymentEntity(id.copy(), bookingId.copy(), moneyAmount.copy(), creditCardNumber.copy(), cardVerificationCode.copy(),
                LocalDateTimeUtils.copyLocalDateTime(expirationDate), email.copy(), name,
                LocalDateTimeUtils.copyLocalDateTime(createdAt));
    }

    // Getter ----------------------------------------------------------------------------------------------------------


    public Id getId() {
        return id.copy();
    }

    public Id getBookingId() {
        return bookingId.copy();
    }

    public MoneyAmount getMoneyAmount() {
        return moneyAmount.copy();
    }

    public CreditCardNumber getCreditCardNumber() {
        return creditCardNumber.copy();
    }

    public CardVerificationCode getCardVerificationCode() {
        return cardVerificationCode.copy();
    }

    public Email getEmail() {
        return email.copy();
    }

    public Name getName() {
        return name.copy();
    }

    public LocalDateTime getCreatedAt() {
        return LocalDateTimeUtils.copyLocalDateTime(createdAt);
    }

    public LocalDateTime getExpirationDate() {
        return LocalDateTimeUtils.copyLocalDateTime(expirationDate);
    }

}
