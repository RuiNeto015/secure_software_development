package com.desofs.backend.database.mappers;

import com.desofs.backend.database.models.*;
import com.desofs.backend.domain.aggregates.BookingDomain;
import com.desofs.backend.domain.aggregates.ReviewDomain;
import com.desofs.backend.domain.enums.BookingStatusEnum;
import com.desofs.backend.domain.valueobjects.Event;
import com.desofs.backend.domain.valueobjects.Id;
import com.desofs.backend.domain.valueobjects.IntervalTime;
import com.desofs.backend.dtos.EventDto;
import com.desofs.backend.dtos.FetchBookingDto;
import com.desofs.backend.dtos.FetchReviewDto;
import com.desofs.backend.dtos.IntervalTimeDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class BookingMapper {

    private final ReviewMapper reviewMapper = new ReviewMapper();
    private final EventMapper eventMapper = new EventMapper();

    public FetchBookingDto domainToDto(BookingDomain booking, String propertyId) {
        FetchReviewDto reviewDto = booking.getReview() == null ? null : reviewMapper.domainToDto(booking.getReview());
        return new FetchBookingDto(
                booking.getId().value(),
                booking.getAccountId().value(),
                propertyId,
                new IntervalTimeDto(booking.getIntervalTime().getFrom(), booking.getIntervalTime().getTo()),
                booking.getEventList().stream().map(event -> new EventDto(event.getDatetime(), event.getState())).toList(),
                booking.getStatus(),
                reviewDto,
                booking.getCreatedAt());
    }

    public BookingDB domainToDb(BookingDomain booking, String propertyId) {
        return new BookingDB(
                booking.getId().value(),
                booking.getAccountId().value(),
                propertyId,
                booking.getIntervalTime().getFrom(),
                booking.getIntervalTime().getTo(),
                booking.getCreatedAt());
    }

    public BookingDomain dbToDomain(BookingDB booking, ReviewDB reviewDB, List<ImageUrlDB> imageUrlDB) {
        ReviewDomain reviewDomain = reviewDB == null ? null : reviewMapper.dbToDomain(reviewDB, imageUrlDB);
        return new BookingDomain(
                Id.create(booking.getId()),
                Id.create(booking.getAccountId()),
                IntervalTime.create(booking.getFromDate(), booking.getToDate()),
                new ArrayList<>(List.of(Event.create(LocalDateTime.now(), BookingStatusEnum.BOOKED))),
                reviewDomain,
                LocalDateTime.now());
    }

    public BookingDomain dbToDomain(BookingDB booking, ReviewDB reviewDB, List<ImageUrlDB> imageUrlDB,
                                    List<EventDB> eventsDB) {
        ReviewDomain reviewDomain = reviewDB == null ? null : reviewMapper.dbToDomain(reviewDB, imageUrlDB);
        return new BookingDomain(
                Id.create(booking.getId()),
                Id.create(booking.getAccountId()),
                IntervalTime.create(booking.getFromDate(), booking.getToDate()),
                eventsDB.stream().map(this.eventMapper::dbToDomain).toList(),
                reviewDomain,
                LocalDateTime.now());
    }
}
