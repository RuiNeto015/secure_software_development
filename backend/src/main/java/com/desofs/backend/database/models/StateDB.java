package com.desofs.backend.database.models;

import com.desofs.backend.domain.enums.BookingStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;

@Entity(name = "state")
@Getter
public class StateDB {

    @Id
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatusEnum value;

    public StateDB() {
    }

    public StateDB(BookingStatusEnum value) {
        this.id = value.getId();
        this.value = value;
    }

}