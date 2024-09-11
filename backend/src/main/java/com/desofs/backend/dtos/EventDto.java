package com.desofs.backend.dtos;

import com.desofs.backend.domain.enums.BookingStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
@AllArgsConstructor
public class EventDto {

    private final LocalDateTime datetime;

    private final BookingStatusEnum state;
}
