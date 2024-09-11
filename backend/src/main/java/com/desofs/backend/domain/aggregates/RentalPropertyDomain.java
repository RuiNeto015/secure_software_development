package com.desofs.backend.domain.aggregates;

import com.desofs.backend.domain.enums.BookingStatusEnum;
import com.desofs.backend.domain.valueobjects.*;
import com.desofs.backend.dtos.CreateRentalPropertyDto;
import com.desofs.backend.dtos.PriceNightIntervalDto;
import com.desofs.backend.dtos.UpdateRentalPropertyDto;
import com.desofs.backend.utils.IntervalTimeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

public class RentalPropertyDomain {
    private final Id id;
    private final Id propertyOwner;
    private final PropertyName propertyName;
    private final Location location;
    private final PositiveInteger maxGuests;
    private final PositiveInteger numBedrooms;
    private final PositiveInteger numBathrooms;
    private final PropertyDescription propertyDescription;
    private final MoneyAmount defaultNightPrice;
    private final List<PriceNightInterval> priceNightIntervalList;
    private final List<BookingDomain> bookingList;
    private boolean isActive;

    // Constructors ----------------------------------------------------------------------------------------------------

    public RentalPropertyDomain(Id id, Id propertyOwner, PropertyName propertyName, Location location,
                                PositiveInteger maxGuests, PositiveInteger numBedrooms, PositiveInteger numBathrooms,
                                PropertyDescription propertyDescription, MoneyAmount defaultNightPrice,
                                List<PriceNightInterval> priceNightIntervalList, List<BookingDomain> bookingList,
                                boolean isActive) {
        notNull(id, "Id must not be null.");
        notNull(propertyOwner, "PropertyOwner must not be null.");
        notNull(propertyName, "PropertyName must not be null.");
        notNull(location, "Location must not be null.");
        notNull(maxGuests, "MaxGuests must not be null.");
        notNull(numBedrooms, "NumBedrooms must not be null.");
        notNull(numBathrooms, "NumBathrooms must not be null.");
        notNull(propertyDescription, "PropertyDescription must not be null.");
        notNull(defaultNightPrice, "Amount must not be null.");
        notNull(priceNightIntervalList, "PriceNightIntervalList must not be null.");
        notNull(bookingList, "BookingList must not be null.");
        isTrue(!IntervalTimeUtils.listHasOverlap(priceNightIntervalList.stream().map(intervalDto ->
                        IntervalTime.create(intervalDto.getInterval().getFrom(), intervalDto.getInterval().getTo())).toList()),
                "The time interval list contain overlaps.");

        this.id = id.copy();
        this.propertyOwner = propertyOwner.copy();
        this.propertyName = propertyName.copy();
        this.location = location.copy();
        this.maxGuests = maxGuests.copy();
        this.numBedrooms = numBedrooms.copy();
        this.numBathrooms = numBathrooms.copy();
        this.propertyDescription = propertyDescription.copy();
        this.defaultNightPrice = defaultNightPrice.copy();
        this.priceNightIntervalList = priceNightIntervalList;
        this.bookingList = bookingList;
        this.isActive = isActive;
    }

    // Used to create a rental property
    public RentalPropertyDomain(CreateRentalPropertyDto dto, String userId) {
        notNull(userId, "PropertyOwner must not be null.");
        notNull(dto.getPropertyName(), "PropertyName must not be null.");
        notNull(dto.getLocation(), "Location must not be null.");
        notNull(dto.getPropertyDescription(), "PropertyDescription must not be null.");
        notNull(dto.getAmount(), "Amount must not be null.");
        notNull(dto.getPriceNightIntervalList(), "PriceNightIntervalList must not be null.");
        isTrue(!IntervalTimeUtils.listHasOverlap(dto.getPriceNightIntervalList().stream().map(intervalDto ->
                        IntervalTime.create(intervalDto.getInterval().getFrom(), intervalDto.getInterval().getTo())).toList()),
                "The time interval contain overlaps.");

        this.id = Id.create(UUID.randomUUID().toString());
        this.propertyOwner = Id.create(userId);
        this.propertyName = PropertyName.create(dto.getPropertyName());
        this.location = Location.create(dto.getLocation().getLat(), dto.getLocation().getLon());
        this.maxGuests = PositiveInteger.create(dto.getMaxGuests());
        this.numBedrooms = PositiveInteger.create(dto.getNumBedrooms());
        this.numBathrooms = PositiveInteger.create(dto.getNumBathrooms());
        this.propertyDescription = PropertyDescription.create(dto.getPropertyDescription());
        this.defaultNightPrice = MoneyAmount.create(dto.getAmount());
        this.priceNightIntervalList = getPriceNightIntervalsList(id, dto.getPriceNightIntervalList());
        this.bookingList = new ArrayList<>();
        this.isActive = true;
    }

    private static List<PriceNightInterval> getPriceNightIntervalsList(Id id, List<PriceNightIntervalDto> dtoList) {
        return dtoList.stream().map(value -> {
            MoneyAmount tempMoneyAmount = MoneyAmount.create(value.getPrice());
            IntervalTime tempIntervalTime = IntervalTime.create(value.getInterval().getFrom(), value.getInterval().getTo());
            return new PriceNightInterval(id, tempMoneyAmount, tempIntervalTime);
        }).toList();
    }

    // Getters ---------------------------------------------------------------------------------------------------------

    public Id getId() {
        return id.copy();
    }

    public Id getPropertyOwner() {
        return propertyOwner.copy();
    }

    public PropertyName getPropertyName() {
        return propertyName.copy();
    }

    public Location getLocation() {
        return location.copy();
    }

    public PositiveInteger getMaxGuests() {
        return maxGuests.copy();
    }

    public PositiveInteger getNumBedrooms() {
        return numBedrooms.copy();
    }

    public PositiveInteger getNumBathrooms() {
        return numBathrooms.copy();
    }

    public PropertyDescription getPropertyDescription() {
        return propertyDescription.copy();
    }

    public MoneyAmount getDefaultNightPrice() {
        return defaultNightPrice.copy();
    }

    public List<PriceNightInterval> getPriceNightIntervalList() {
        return List.copyOf(priceNightIntervalList);
    }

    public List<BookingDomain> getBookingList() {
        return List.copyOf(bookingList);
    }

    public boolean getIsActive() {
        return isActive;
    }

    // Domain methods --------------------------------------------------------------------------------------------------

    /**
     * Checks if the booked bookings has compatible intervals
     */
    private boolean canTimeIntervalsBeUpdate(List<PriceNightInterval> newList) {
        return this.bookingList.stream()
                .filter(bookingDomain -> bookingDomain.getStatus() == BookingStatusEnum.BOOKED)
                .allMatch(booking ->
                        newList.stream().anyMatch(newInterval ->
                                IntervalTimeUtils.intervalsIntercept(newInterval.getInterval(), booking.getIntervalTime())
                        )
                );
    }

    public RentalPropertyDomain update(UpdateRentalPropertyDto dto) {
        if (!this.isActive) {
            throw new IllegalArgumentException("The rental property is already deactivated.");
        }

        if (!this.canTimeIntervalsBeUpdate(getPriceNightIntervalsList(this.id, dto.getPriceNightIntervalList()))) {
            throw new IllegalArgumentException("The intervals are not valid.");
        }

        return new RentalPropertyDomain(this.id.copy(),
                this.propertyOwner.copy(),
                PropertyName.create(dto.getPropertyName()),
                Location.create(dto.getLocation().getLat(), dto.getLocation().getLon()),
                PositiveInteger.create(dto.getMaxGuests()),
                PositiveInteger.create(dto.getNumBedrooms()),
                PositiveInteger.create(dto.getNumBathrooms()),
                PropertyDescription.create(dto.getPropertyDescription()),
                MoneyAmount.create(dto.getAmount()),
                getPriceNightIntervalsList(this.id, dto.getPriceNightIntervalList()),
                this.bookingList,
                this.isActive);
    }

    private void bookingCanBeAdded(BookingDomain bookingDomain) {
        if (!this.isActive) {
            throw new IllegalArgumentException("The rental property is deactivated.");
        }

        boolean bookingIsUnique = this.bookingList.stream()
                .filter(b -> b.getId().value().equals(bookingDomain.getId().value()))
                .toList()
                .isEmpty();

        if (!bookingIsUnique) {
            throw new IllegalArgumentException("The booking already exists.");
        }
    }

    public void addBooking(BookingDomain bookingDomain) {
        bookingCanBeAdded(bookingDomain);

        this.bookingList.add(bookingDomain);
    }

    public Double getAverageStars() {
        return this.bookingList.stream().mapToDouble(b -> b.getReview().getStars().value()).average().orElse(0.0);
    }

    public RentalPropertyDomain deactivate() {
        if (this.isActive) {
            this.isActive = false;
            return this;
        } else {
            throw new IllegalArgumentException("The rental property is already deactivated.");
        }
    }

}
