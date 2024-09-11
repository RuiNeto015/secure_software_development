package com.desofs.backend.dtos;

import com.desofs.backend.domain.enums.BookingStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class FetchBookingDto {

    private final String id;

    private final String accountId;

    private final String propertyId;

    private IntervalTimeDto intervalTime;

    private List<EventDto> eventList;

    private BookingStatusEnum status;

    private FetchReviewDto review;

    private LocalDateTime createdAt;
}
