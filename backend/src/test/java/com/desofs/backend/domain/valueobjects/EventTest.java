package com.desofs.backend.domain.valueobjects;

import com.desofs.backend.domain.enums.BookingStatusEnum;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EventTest {

    @Test
    public void testEventCreation() {
        LocalDateTime datetime = LocalDateTime.of(2024, 5, 13, 10, 0);
        BookingStatusEnum state = BookingStatusEnum.COMPLETED;
        Event event = Event.create(datetime, state);

        assertEquals(datetime, event.getDatetime());
        assertEquals(state, event.getState());
    }

    @Test
    public void testNullDateTime() {
        BookingStatusEnum state = BookingStatusEnum.COMPLETED;
        assertThrows(NullPointerException.class, () -> Event.create(null, state));
    }

    @Test
    public void testNullState() {
        LocalDateTime datetime = LocalDateTime.of(2024, 5, 13, 10, 0);
        assertThrows(NullPointerException.class, () -> Event.create(datetime, null));
    }

    @Test
    public void testImmutableDateTime() {
        LocalDateTime datetime = LocalDateTime.of(2024, 5, 13, 10, 0);
        BookingStatusEnum state = BookingStatusEnum.COMPLETED;
        Event event = Event.create(datetime, state);

        // Modify the original datetime after creating the event
        datetime = LocalDateTime.of(2024, 5, 14, 12, 0);
        LocalDateTime dateTimeChanged = event.getDatetime().minusHours(5);

        // Check if the datetime of the event remains unchanged
        assertEquals(LocalDateTime.of(2024, 5, 13, 10, 0), event.getDatetime());
    }
}
