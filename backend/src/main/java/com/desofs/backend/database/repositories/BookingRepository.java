package com.desofs.backend.database.repositories;

import com.desofs.backend.database.mappers.BookingMapper;
import com.desofs.backend.database.models.*;
import com.desofs.backend.database.springRepositories.*;
import com.desofs.backend.domain.aggregates.BookingDomain;
import com.desofs.backend.domain.aggregates.RentalPropertyDomain;
import com.desofs.backend.domain.valueobjects.Event;
import com.desofs.backend.domain.valueobjects.Id;
import com.desofs.backend.exceptions.DatabaseException;
import com.desofs.backend.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BookingRepository {

    private final BookingRepositoryJPA bookingRepositoryJPA;

    private final PaymentRepositoryJPA paymentRepositoryJPA;

    private final ReviewRepositoryJPA reviewRepositoryJPA;

    private final ImageRepositoryJPA imageRepositoryJPA;

    private final EventRepositoryJPA eventRepositoryJPA;

    private final RentalPropertyRepository rentalPropertyRepository;

    private final BookingMapper bookingMapper;

    public void create(BookingDomain bookingDomain, Id propertyId) throws DatabaseException {
        BookingDB bookingDB = this.bookingRepositoryJPA.findById(bookingDomain.getId().value()).orElse(null);

        if (bookingDB != null) {
            throw new DatabaseException("Duplicated ID violation.");
        }

        bookingDB = this.bookingRepositoryJPA.save(this.bookingMapper.domainToDb(bookingDomain, propertyId.value()));

        for (Event event : bookingDomain.getEventList()) {
            StateDB stateDB = new StateDB(event.getState());
            EventDB eventDB = new EventDB(UUID.randomUUID().toString(), bookingDB, event.getDatetime(), stateDB);
            this.eventRepositoryJPA.save(eventDB);
        }
    }

    public void updateEvents(BookingDomain bookingDomain) throws NotFoundException {
        BookingDB bookingDB = this.bookingRepositoryJPA.findById(bookingDomain.getId().value()).orElse(null);

        if (bookingDB == null) {
            throw new NotFoundException("ID not found.");
        }

        var toInsertEventList = bookingDomain.getEventList().stream().filter(e ->
                bookingDB.getEvents().stream().allMatch(eDB -> eDB.getState().getValue() != e.getState())).toList();

        for (Event event : toInsertEventList) {
            StateDB stateDB = new StateDB(event.getState());
            EventDB eventDB = new EventDB(UUID.randomUUID().toString(), bookingDB, event.getDatetime(), stateDB);
            this.eventRepositoryJPA.save(eventDB);
        }
    }

    public BookingDomain findById(String bookingId) {
        try {
            BookingDB bookingDB = this.bookingRepositoryJPA.findById(bookingId).orElse(null);

            if (bookingDB != null) {
                ReviewDB reviewDB = this.reviewRepositoryJPA.findByBookingId(bookingId);

                if (reviewDB != null) {
                    List<ImageUrlDB> imagesUrlsDB = this.imageRepositoryJPA.findByReviewId(reviewDB.getId());
                    return this.bookingMapper.dbToDomain(bookingDB, reviewDB, imagesUrlsDB, bookingDB.getEvents());
                } else {
                    return this.bookingMapper.dbToDomain(bookingDB, null, null, bookingDB.getEvents());
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public List<BookingDomain> findAllByUserId(String userId) {
        List<BookingDomain> result = new ArrayList<>();
        try {
            List<BookingDB> bookingDB = this.bookingRepositoryJPA.findAllByAccountId(userId);

            for (BookingDB bookingId : bookingDB) {
                result.add(this.findById(bookingId.getId()));
            }
            return result;
        } catch (Exception e) {
            return result;
        }
    }

    public int clearBookingWhereCheckoutDatePassed() {
        List<BookingDB> bookings = this.bookingRepositoryJPA.findAllBookingWhereCheckoutDatePassed();
        for (BookingDB booking : bookings) {
            var domain = this.findById(booking.getId());
            try {
                this.updateEvents(domain.terminate());
            } catch (NotFoundException ignored) {
            }
        }
        return bookings.size();
    }

    public RentalPropertyDomain findRentalProperty(String bookingId) throws NotFoundException {
        BookingDB bookingDB = this.bookingRepositoryJPA.findById(bookingId).orElse(null);

        if (bookingDB == null) {
            throw new NotFoundException("Booking not found");
        }

        String propertyId = bookingDB.getPropertyId();
        return this.rentalPropertyRepository.findById(propertyId);
    }

    public List<BookingDomain> findAllByRentalPropertyId(String rentalPropertyId) {
        List<BookingDomain> result = new ArrayList<>();
        try {
            List<BookingDB> bookingDB = this.bookingRepositoryJPA.findAllByPropertyId(rentalPropertyId);

            for (BookingDB bookingId : bookingDB) {
                result.add(this.findById(bookingId.getId()));
            }
            return result;
        } catch (Exception e) {
            return result;
        }
    }
}
