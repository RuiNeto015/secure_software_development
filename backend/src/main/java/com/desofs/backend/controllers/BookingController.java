package com.desofs.backend.controllers;

import com.desofs.backend.dtos.CreateStripeSessionDto;
import com.desofs.backend.dtos.FetchBookingDto;
import com.desofs.backend.dtos.FetchStripeSessionDto;
import com.desofs.backend.exceptions.NotFoundException;
import com.desofs.backend.services.BookingService;
import com.desofs.backend.services.LoggerService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Booking Controller", description = "Endpoints for managing bookings")
@RestController
@RequestMapping(path = "/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final LoggerService logger;

    @Value("${stripe.webhookSecret}")
    private String endpointSecret;

    @GetMapping("/{id}")
    public ResponseEntity<FetchBookingDto> getById(@PathVariable final String id) throws NotFoundException {
        FetchBookingDto booking = this.bookingService.findById(id);
        logger.info("Fetched booking with ID: " + id);
        return ResponseEntity.ok().body(booking);
    }

    @GetMapping("/getAllByUser/{id}")
    public ResponseEntity<List<FetchBookingDto>> getAllByUserId(@PathVariable final String id) throws NotFoundException {
        List<FetchBookingDto> rentalProperty = this.bookingService.findAllByUserId(id);
        logger.info("Fetched " + rentalProperty.size() + " bookings for user ID: " + id);
        return ResponseEntity.ok().body(rentalProperty);
    }

    @PostMapping("/stripe-checkout")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<FetchStripeSessionDto> createCheckout(@RequestBody final CreateStripeSessionDto createCheckoutDto,
                                                                Authentication authentication)
            throws Exception {
        String userId = authentication.getName();
        logger.info("Creating Stripe checkout session for user ID: " + userId);
        FetchStripeSessionDto sessionDto = this.bookingService.createStripeCheckoutSession(createCheckoutDto, userId);
        logger.info("Created Stripe checkout session for user ID: " + userId);
        return new ResponseEntity<>(sessionDto, HttpStatus.OK);
    }

    @PostMapping("/stripe-webhook")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<FetchBookingDto> create(final HttpServletRequest request) throws Exception {
        String payload = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        String sigHeader = request.getHeader("Stripe-Signature");

        try {
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
            StripeObject stripeObject = dataObjectDeserializer.getObject().orElse(null);

            if (stripeObject instanceof PaymentIntent paymentIntent) {
                this.bookingService.create(paymentIntent);
                logger.info("Processed Stripe payment intent");
            } else {
                logger.warn("Unhandled Stripe event type: " + event.getType());
            }

        } catch (SignatureVerificationException e) {
            logger.error("Stripe signature verification failed");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<FetchBookingDto> cancel(@PathVariable final String id) throws NotFoundException {
        FetchBookingDto bookingDto = this.bookingService.cancel(id);
        logger.info("Cancelled booking with ID: " + id);
        return new ResponseEntity<>(bookingDto, HttpStatus.CREATED);
    }

}
