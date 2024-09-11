package com.desofs.backend.domain.aggregates;

import com.desofs.backend.domain.enums.BookingStatusEnum;
import com.desofs.backend.domain.valueobjects.Event;
import com.desofs.backend.domain.valueobjects.Id;
import com.desofs.backend.domain.valueobjects.IntervalTime;
import com.desofs.backend.dtos.CreateBookingDto;
import com.desofs.backend.dtos.IntervalTimeDto;
import com.desofs.backend.utils.ListUtils;
import com.desofs.backend.utils.LocalDateTimeUtils;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

public class BookingDomain {

    public static int MAX_DAYS_TO_REFUND = 5;

    private final Id id;
    private final Id accountId;
    private final IntervalTime intervalTime;
    private final List<Event> eventList;
    private ReviewDomain review;
    @Getter
    private final LocalDateTime createdAt;

    // Constructors ----------------------------------------------------------------------------------------------------

    public BookingDomain(Id id, Id accountId, IntervalTime intervalTime,
                         List<Event> eventList, ReviewDomain review, LocalDateTime createdAt) {
        notNull(id, "Id must not be null.");
        notNull(accountId, "AccountId must not be null.");
        notNull(intervalTime, "IntervalTime must not be null.");
        notNull(eventList, "EventList must not be null.");
        isTrue(eventListIsValid(eventList), "EventList can't contain duplicates.");
        notNull(createdAt, "CreatedAt must not be null.");


        this.id = id.copy();
        this.accountId = accountId.copy();
        this.intervalTime = intervalTime.copy();
        this.eventList = new ArrayList<>(eventList);
        this.review = review == null ? null : review.copy();
        this.createdAt = LocalDateTimeUtils.copyLocalDateTime(createdAt);
    }

    public BookingDomain(IntervalTimeDto intervalTime, Id bookingId, String userId) {
        this(bookingId,
                Id.create(userId),
                IntervalTime.create(intervalTime.getFrom(), intervalTime.getTo()),
                new ArrayList<>(List.of(Event.create(LocalDateTime.now(), BookingStatusEnum.BOOKED))),
                null,
                LocalDateTime.now());
    }

    private static boolean eventListIsValid(List<Event> eventList) {
        return eventList.isEmpty() || !ListUtils.hasDuplicates(eventList);
    }

    // Getters ---------------------------------------------------------------------------------------------------------

    public Id getId() {
        return id.copy();
    }

    public Id getAccountId() {
        return accountId.copy();
    }

    public IntervalTime getIntervalTime() {
        return intervalTime.copy();
    }

    public List<Event> getEventList() {
        return List.copyOf(eventList);
    }

    public ReviewDomain getReview() {
        return this.review == null ? null : review.copy();
    }

    public BookingStatusEnum getStatus() {
        Event event = this.eventList.stream().max(Comparator.comparing(Event::getDatetime)).stream().findFirst().orElse(null);
        if (event != null) {
            return event.getState();
        }
        return BookingStatusEnum.BOOKED;
    }

    // Domain methods --------------------------------------------------------------------------------------------------

    public boolean checkoutPassed() {
        Date now = new Date();
        return (now.equals(this.intervalTime.getTo()) || now.after(this.intervalTime.getTo()));
    }

    public int daysUntilCheckout() {
        Date now = new Date();

        long differenceInMillis = this.intervalTime.getTo().getTime() - now.getTime();
        return (int) TimeUnit.MILLISECONDS.toDays(differenceInMillis);
    }

    private void addEvent(Event event) {
        boolean alreadyExists = !this.eventList.stream().filter(e -> e.getState() == event.getState()).toList().isEmpty();
        if (alreadyExists) {
            throw new IllegalArgumentException("The event type already exists.");
        }
        this.eventList.add(event);
    }

    private void checkStatus() {
        if (this.getStatus() == BookingStatusEnum.COMPLETED) {
            throw new IllegalArgumentException("The booking is already completed.");
        }

        if (this.getStatus() == BookingStatusEnum.CANCELLED) {
            throw new IllegalArgumentException("The booking is already cancelled.");
        }

        if (this.getStatus() == BookingStatusEnum.REFUNDED) {
            throw new IllegalArgumentException("The booking it was already refunded.");
        }
    }

    public BookingDomain cancel() {
        this.checkStatus();

        if (this.checkoutPassed()) {
            throw new IllegalArgumentException("The checkout date has passed.");
        }

        if (this.daysUntilCheckout() >= MAX_DAYS_TO_REFUND) {
            this.addEvent(Event.create(LocalDateTime.now(), BookingStatusEnum.REFUNDED));
        } else {
            this.addEvent(Event.create(LocalDateTime.now(), BookingStatusEnum.CANCELLED));
        }

        return this;
    }

    public BookingDomain terminate() {
        this.checkStatus();

        if (!this.checkoutPassed()) {
            throw new IllegalArgumentException("The checkout date did not passed yet.");
        }

        this.addEvent(Event.create(LocalDateTime.now(), BookingStatusEnum.COMPLETED));

        return this;
    }

    public void writeReview(ReviewDomain review) {
        if (this.review != null) {
            throw new IllegalArgumentException("There is already a review associated with the booking");
        }
        this.review = review;
    }

}
