package com.desofs.backend.database.repositories;

import com.desofs.backend.database.mappers.BookingMapper;
import com.desofs.backend.database.mappers.PriceNightIntervalMapper;
import com.desofs.backend.database.mappers.RentalPropertyMapper;
import com.desofs.backend.database.models.*;
import com.desofs.backend.database.springRepositories.*;
import com.desofs.backend.domain.aggregates.BookingDomain;
import com.desofs.backend.domain.aggregates.RentalPropertyDomain;
import com.desofs.backend.domain.valueobjects.PriceNightInterval;
import com.desofs.backend.exceptions.DatabaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("RentalPropertyRepositoryCapsule")
@RequiredArgsConstructor
public class RentalPropertyRepository {

    private final RentalPropertyRepositoryJPA rentalPropertyRepositoryJPA;

    private final PriceNightIntervalRepositoryJPA priceNightIntervalRepositoryJPA;

    private final BookingRepositoryJPA bookingRepositoryJPA;

    private final PaymentRepositoryJPA paymentRepositoryJPA;

    private final ReviewRepositoryJPA reviewRepositoryJPA;

    private final PriceNightIntervalRepository priceNightIntervalRepository;

    private final RentalPropertyMapper rentalPropertyMapper;

    private final ImageRepositoryJPA imageRepositoryJPA;

    private final PriceNightIntervalMapper priceNightIntervalMapper;

    private final BookingMapper bookingMapper;

    public void create(RentalPropertyDomain rentalProperty) throws DatabaseException {
        Optional<RentalPropertyDB> rentalPropertyById =
                this.rentalPropertyRepositoryJPA.findById(rentalProperty.getId().value());

        if (rentalPropertyById.isPresent()) {
            throw new DatabaseException("Duplicated ID violation.");
        }

        this.rentalPropertyRepositoryJPA.save(this.rentalPropertyMapper.toDatabaseObject(rentalProperty));
    }

    public RentalPropertyDomain update(RentalPropertyDomain rentalProperty) throws DatabaseException {
        RentalPropertyDB rentalPropertyById =
                this.rentalPropertyRepositoryJPA.findById(rentalProperty.getId().value()).orElse(null);

        if (rentalPropertyById == null) {
            throw new DatabaseException("ID not found.");
        }

        RentalPropertyDB updated = this.rentalPropertyRepositoryJPA.save(this.rentalPropertyMapper.toDatabaseObject(rentalProperty));

        List<PriceNightIntervalDB> oldIntervals = this.priceNightIntervalRepositoryJPA.findByRentalPropertyId(rentalProperty.getId().value());
        this.priceNightIntervalRepositoryJPA.deleteAll(oldIntervals);

        for (PriceNightInterval interval : rentalProperty.getPriceNightIntervalList()) {
            this.priceNightIntervalRepository.create(interval);
        }

        return this.rentalPropertyMapper.toDomainObject(updated, rentalProperty.getPriceNightIntervalList(), rentalProperty.getBookingList());
    }

    public RentalPropertyDomain findById(String id) {
        try {
            Optional<RentalPropertyDB> rentalProperty = this.rentalPropertyRepositoryJPA.findById(id);
            if (rentalProperty.isPresent()) {
                return rentalProperty.map(rp -> this.rentalPropertyMapper.toDomainObject(rp, joinPriceNightIntervals(rp),
                        joinBookings(rp))).orElse(null);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public List<RentalPropertyDomain> findAllByUserId(String id) {
        try {
            List<RentalPropertyDB> rentalPropertyList = this.rentalPropertyRepositoryJPA.findAllByPropertyOwnerId(id);

            return rentalPropertyList.stream().map(rp -> this.rentalPropertyMapper.toDomainObject(rp, joinPriceNightIntervals(rp),
                    joinBookings(rp))).toList();
        } catch (Exception e) {
            return null;
        }
    }

    public List<RentalPropertyDomain> findAll() {
        List<RentalPropertyDomain> rentalPropertyList = new ArrayList<>();
        this.rentalPropertyRepositoryJPA.findAll().forEach(rp ->
                rentalPropertyList.add(this.rentalPropertyMapper.toDomainObject(rp, joinPriceNightIntervals(rp),
                        joinBookings(rp))));
        return rentalPropertyList;
    }

    private List<PriceNightInterval> joinPriceNightIntervals(RentalPropertyDB rp) {
        List<PriceNightIntervalDB> priceNightIntervalsDB =
                priceNightIntervalRepositoryJPA.findByRentalPropertyId(rp.getId());

        return priceNightIntervalsDB.stream()
                .map(priceNightIntervalMapper::dbToDomain)
                .collect(Collectors.toList());
    }

    private List<BookingDomain> joinBookings(RentalPropertyDB rp) {
        List<BookingDB> bookingsDB = bookingRepositoryJPA.findByPropertyId(rp.getId());

        return bookingsDB.stream()
                .map(booking -> {
                    PaymentDB paymentDB = this.paymentRepositoryJPA.findByBookingId(booking.getId()).orElse(null);
                    ReviewDB reviewDB = this.reviewRepositoryJPA.findByBookingId(booking.getId());
                    if (reviewDB != null) {
                        List<ImageUrlDB> imagesUrlsDB = this.imageRepositoryJPA.findByReviewId(reviewDB.getId());
                        return this.bookingMapper.dbToDomain(booking, reviewDB, imagesUrlsDB);
                    } else {
                        return this.bookingMapper.dbToDomain(booking, null, null);
                    }
                })
                .collect(Collectors.toList());
    }
}


