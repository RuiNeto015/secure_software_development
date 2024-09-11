package com.desofs.backend.database.mappers;

import com.desofs.backend.database.models.PaymentDB;
import com.desofs.backend.domain.entities.PaymentEntity;
import com.desofs.backend.domain.valueobjects.*;
import com.desofs.backend.dtos.CreatePaymentDto;
import com.desofs.backend.dtos.PaymentDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class PaymentMapper {

    public PaymentEntity dtoToDomain(CreatePaymentDto payment, String bookingId) {
        return new PaymentEntity(
                Id.create(UUID.randomUUID().toString()),
                Id.create(bookingId),
                MoneyAmount.create(payment.getMoneyAmount()),
                CreditCardNumber.create(payment.getCreditCardNumber()),
                CardVerificationCode.create(payment.getCardVerificationCode()),
                payment.getExpirationDate(),
                Email.create(payment.getEmail()),
                Name.create(payment.getPersonName()),
                payment.getCreatedAt());
    }

    public PaymentDto domainToDto(PaymentEntity payment) {
        return new PaymentDto(
                payment.getId().value(),
                payment.getBookingId().value(),
                payment.getMoneyAmount().value(),
                payment.getCreditCardNumber().value(),
                payment.getCardVerificationCode().value(),
                payment.getExpirationDate(),
                payment.getEmail().value(),
                payment.getName().value(),
                payment.getCreatedAt());
    }

    public PaymentEntity dbToDomain(PaymentDB payment) {
        return new PaymentEntity(
                Id.create(UUID.randomUUID().toString()),
                Id.create(payment.getBookingId()),
                MoneyAmount.create(BigDecimal.valueOf(payment.getTotal())),
                CreditCardNumber.create(payment.getCreditCardNumber()),
                CardVerificationCode.create(payment.getCvc()),
                payment.getExpirationDate(),
                Email.create(payment.getEmail()),
                Name.create(payment.getNameOnCard()),
                payment.getCreatedAt());
    }

    public PaymentDB domainToDb(PaymentEntity payment) {
        return new PaymentDB(
                payment.getId().value(),
                payment.getBookingId().value(),
                payment.getMoneyAmount().value().doubleValue(),
                payment.getCreditCardNumber().value(),
                payment.getCardVerificationCode().value(),
                payment.getExpirationDate(),
                payment.getEmail().value(),
                payment.getName().value(),
                payment.getCreatedAt());
    }
}