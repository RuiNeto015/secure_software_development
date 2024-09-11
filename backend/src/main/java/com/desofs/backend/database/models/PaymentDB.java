package com.desofs.backend.database.models;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity(name = "payment")
@Getter
public class PaymentDB {

    @Id
    private String id;

    @Column(nullable = false)
    private String bookingId;

    @Column(nullable = false)
    private Double total;

    @Column(nullable = false)
    private String creditCardNumber;

    @Column(nullable = false)
    private String cvc;

    @Column(nullable = false)
    private LocalDateTime expirationDate;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String nameOnCard;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public PaymentDB() {
    }

    public PaymentDB(String id, String bookingId, Double total, String creditCardNumber, String cvc,
                     LocalDateTime expirationDate, String email, String nameOnCard, LocalDateTime createdAt) {
        this.id = id;
        this.bookingId = bookingId;
        this.total = total;
        this.creditCardNumber = creditCardNumber;
        this.cvc = cvc;
        this.expirationDate = expirationDate;
        this.email = email;
        this.nameOnCard = nameOnCard;
        this.createdAt = createdAt;
    }
}
