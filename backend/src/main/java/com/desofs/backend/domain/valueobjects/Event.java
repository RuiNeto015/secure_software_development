package com.desofs.backend.domain.valueobjects;

import com.desofs.backend.domain.enums.BookingStatusEnum;
import com.desofs.backend.utils.LocalDateTimeUtils;
import lombok.Getter;

import java.time.LocalDateTime;

import static org.apache.commons.lang3.Validate.notNull;

public class Event {

    private final LocalDateTime datetime;

    @Getter
    private final BookingStatusEnum state;

    private Event(LocalDateTime datetime, BookingStatusEnum state) {
        this.datetime = datetime;
        this.state = state;
    }

    public static Event create(LocalDateTime datetime, BookingStatusEnum state) {
        notNull(datetime,
                "Datetime must not be null.");
        notNull(state,
                "State must not be null.");
        return new Event(LocalDateTimeUtils.copyLocalDateTime(datetime), state);
    }

    public Event copy() {
        return new Event(LocalDateTimeUtils.copyLocalDateTime(datetime), state);
    }

    public LocalDateTime getDatetime() {
        return LocalDateTimeUtils.copyLocalDateTime(this.datetime);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return this.state == ((Event) obj).state;
    }

}