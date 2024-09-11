package com.desofs.backend.services;

import com.desofs.backend.database.mappers.BookingMapper;
import com.desofs.backend.database.repositories.BookingRepository;
import com.desofs.backend.database.repositories.RentalPropertyRepository;
import com.desofs.backend.domain.aggregates.BookingDomain;
import com.desofs.backend.domain.aggregates.RentalPropertyDomain;
import com.desofs.backend.domain.valueobjects.Id;
import com.desofs.backend.domain.valueobjects.IntervalTime;
import com.desofs.backend.domain.valueobjects.MoneyAmount;
import com.desofs.backend.dtos.*;
import com.desofs.backend.exceptions.NotFoundException;
import com.desofs.backend.exceptions.UnavailableTimeInterval;
import com.desofs.backend.utils.IntervalTimeUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData;
import com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData.ProductData;
import com.stripe.param.checkout.SessionCreateParams.PaymentIntentData;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final RentalPropertyRepository rentalPropertyRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final LoggerService logger;
    private final EncryptionService encryptionService;

    @Value("${stripe.apiKey}")
    private String StripeApiKey;

    /**
     * Gets the property or throws exception if not found
     */
    private RentalPropertyDomain getPropertyThrowingError(String propertyId) throws NotFoundException {
        RentalPropertyDomain rentalProperty = this.rentalPropertyRepository.findById(propertyId);
        if (rentalProperty == null) {
            logger.warn("Rental property with ID " + propertyId + " not found");
            throw new NotFoundException("Rental property not found");
        }
        return rentalProperty;
    }

    /*
     * Gets the booking or throws exception if not found
     */
    private BookingDomain getBookingThrowingError(String bookingId) throws NotFoundException {
        BookingDomain bookingDomain = this.bookingRepository.findById(bookingId);
        if (bookingDomain == null) {
            logger.warn("Booking with ID " + bookingId + " not found");
            throw new NotFoundException("Booking not found");
        }
        return bookingDomain;
    }

    private String requestCheckoutSession(long totalPrice, RentalPropertyDomain rentalProperty, String successUrl,
                                          IntervalTimeDto intervalTime, String userId) throws Exception {

        ProductData productData = PriceData.ProductData.builder()
                .setName(rentalProperty.getPropertyName().value())
                .build();

        PriceData priceData = PriceData.builder()
                .setCurrency("eur")
                .setUnitAmount(totalPrice * 100)
                .setProductData(productData)
                .build();

        CheckoutSessionMetadata metadata = new CheckoutSessionMetadata(rentalProperty.getId().value(), intervalTime, userId);
        ObjectMapper objectMapper = new ObjectMapper();
        String encryptedMetadata = encryptionService.encryptMessage(objectMapper.writeValueAsString(metadata));
        int middleOfEncryptedString = 324;
        PaymentIntentData paymentData = PaymentIntentData.builder()
                .putMetadata("firstChunk", encryptedMetadata.substring(0, middleOfEncryptedString))
                .putMetadata("secondChunk", encryptedMetadata.substring(middleOfEncryptedString, encryptedMetadata.length()))
                .build();

        Stripe.apiKey = this.StripeApiKey;
        SessionCreateParams params = SessionCreateParams.builder()
                .setSuccessUrl(successUrl)
                .addLineItem(SessionCreateParams.LineItem.builder().setPriceData(priceData).setQuantity(1L).build())
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setPaymentIntentData(paymentData)
                .build();

        Session session = Session.create(params);
        logger.info("Checkout session requested for rental property with ID " + rentalProperty.getId());
        return session.getUrl();
    }

    public FetchStripeSessionDto createStripeCheckoutSession(CreateStripeSessionDto createCheckoutDto, String userId)
            throws Exception {

        RentalPropertyDomain rentalProperty = getPropertyThrowingError(createCheckoutDto.propertyId());
        List<IntervalTime> unavailableIntervals = rentalProperty.getBookingList().stream()
                .map(BookingDomain::getIntervalTime).toList();

        // Check if requested date interval intercepts with any date already scheduled
        Date from = createCheckoutDto.intervalTime().getFrom();
        Date to = createCheckoutDto.intervalTime().getTo();
        IntervalTime intervalToCompare = IntervalTime.create(from, to);
        for (var interval : unavailableIntervals) {
            if (IntervalTimeUtils.intervalsIntercept(interval, intervalToCompare)) {
                logger.warn("Requested date interval conflicts with an existing booking for property with ID " + rentalProperty.getId());
                throw new UnavailableTimeInterval();
            }
        }

        // calculate price for the stay
        MoneyAmount defaultNightPrice = rentalProperty.getDefaultNightPrice();
        long days = Duration.between(intervalToCompare.getFrom().toInstant(), intervalToCompare.getTo().toInstant()).toDays();
        long totalPrice = days * defaultNightPrice.getValue().longValue();

        // create strip checkout session
        String sessionUrl = requestCheckoutSession(totalPrice, rentalProperty, createCheckoutDto.successUrl(),
                createCheckoutDto.intervalTime(), userId);
        logger.info("Stripe checkout session created successfully for property ID " + rentalProperty.getId());
        return new FetchStripeSessionDto(sessionUrl);
    }

    @Transactional
    public void create(PaymentIntent intent) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String firstChunk = intent.getMetadata().get("firstChunk");
        String secondChunk = intent.getMetadata().get("secondChunk");
        String decryptedMedataAsString = encryptionService.decryptMessage(firstChunk + secondChunk);
        CheckoutSessionMetadata metadata = objectMapper.readValue(decryptedMedataAsString, CheckoutSessionMetadata.class);

        RentalPropertyDomain rentalProperty = getPropertyThrowingError(metadata.propertyId());
        Id bookingId = Id.create(UUID.randomUUID().toString());
        BookingDomain bookingDomain = new BookingDomain(metadata.intervalTime(), bookingId, metadata.userId());
        rentalProperty.addBooking(bookingDomain);
        bookingRepository.create(bookingDomain, rentalProperty.getId());

        logger.info("Booking created with ID " + bookingId.value() + " with payment intent");
    }

    @Transactional
    public FetchBookingDto cancel(String bookingId) throws NotFoundException {
        BookingDomain bookingDomain = this.getBookingThrowingError(bookingId);
        RentalPropertyDomain rentalPropertyDomain = this.bookingRepository.findRentalProperty(bookingId);

        bookingDomain = bookingDomain.cancel();
        bookingRepository.updateEvents(bookingDomain);

        logger.info("Booking with ID " + bookingId + " has been cancelled");

        return this.bookingMapper.domainToDto(bookingDomain, rentalPropertyDomain.getId().value());
    }

    @Transactional
    public List<FetchBookingDto> findAllByUserId(String userId) throws NotFoundException {
        List<BookingDomain> bookingDomain = this.bookingRepository.findAllByUserId(userId);

        logger.info("Fetched all bookings for user " + userId);

        return bookingDomain.stream().map(booking -> {
            RentalPropertyDomain rentalPropertyDomain;
            try {
                rentalPropertyDomain = this.bookingRepository.findRentalProperty(booking.getId().value());
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
            return this.bookingMapper.domainToDto(booking, rentalPropertyDomain.getId().value());
        }).toList();
    }

    @Transactional
    public FetchBookingDto findById(String bookingId) throws NotFoundException {
        BookingDomain bookingDomain = getBookingThrowingError(bookingId);
        RentalPropertyDomain rentalPropertyDomain = this.bookingRepository.findRentalProperty(bookingId);

        logger.info("Fetched booking with ID " + bookingId);

        return bookingMapper.domainToDto(bookingDomain, rentalPropertyDomain.getId().value());
    }

    @Scheduled(cron = "* * 3 * * *")
    public void checkBookingsThatCheckoutPassed() {
        try {
            var count = this.bookingRepository.clearBookingWhereCheckoutDatePassed();
            logger.info("Cleared " + count + " bookings where checkout date has passed");
        } catch (Exception e) {
            logger.error("Error clearing bookings where checkout date has passed");
        }
    }
}
