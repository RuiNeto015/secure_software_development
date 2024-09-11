package com.desofs.backend.controllers;

import com.desofs.backend.domain.enums.Authority;
import com.desofs.backend.dtos.CreateReviewDto;
import com.desofs.backend.dtos.FetchReviewDto;
import com.desofs.backend.exceptions.DatabaseException;
import com.desofs.backend.exceptions.NotAuthorizedException;
import com.desofs.backend.exceptions.NotFoundException;
import com.desofs.backend.services.ReviewService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.desofs.backend.config.UserDetailsConfig.hasAuthorization;

@Tag(name = "Review Controller", description = "Endpoints for managing reviews")
@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/review/add")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<FetchReviewDto> create(
            @RequestParam("bookingId") String bookingId,
            @RequestParam("text") String text,
            @RequestParam("stars") int stars,
            @RequestParam("images") ArrayList<MultipartFile> images,
            Authentication authentication)
            throws DatabaseException, NotFoundException, NotAuthorizedException, IOException {

        CreateReviewDto createReviewDto = new CreateReviewDto(bookingId, text, stars, images);
        String userId = authentication.getName();
        FetchReviewDto reviewDto = this.reviewService.create(createReviewDto, userId);

        return new ResponseEntity<>(reviewDto, HttpStatus.CREATED);
    }

    @PostMapping("/review/change_state")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> create(@RequestParam String reviewId, @RequestParam String state)
            throws DatabaseException, NotFoundException {

        FetchReviewDto reviewDto = this.reviewService.changeState(reviewId, state);
        return new ResponseEntity<>(reviewDto, HttpStatus.OK);
    }

    @PostMapping("/review/getByPropertyId")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> create(@RequestParam String propertyId)
            throws DatabaseException, NotFoundException {

        List<FetchReviewDto> reviewsDto = this.reviewService.getAllByRentalPropertyId(propertyId);
        return new ResponseEntity<>(reviewsDto, HttpStatus.OK);
    }

    @GetMapping("/review/{propertyId}")
    public ResponseEntity<?> getByPropertyId(@PathVariable String propertyId, Authentication authentication)
            throws NotFoundException {

        List<FetchReviewDto> reviewsDto;

        if (hasAuthorization(authentication, Authority.BUSINESSADMIN)) {
            reviewsDto = this.reviewService.getAllByRentalPropertyId(propertyId);
            return new ResponseEntity<>(reviewsDto, HttpStatus.OK);
        } else {
            reviewsDto = this.reviewService.getAllActiveByRentalPropertyId(propertyId);
        }

        return new ResponseEntity<>(reviewsDto, HttpStatus.OK);
    }
}