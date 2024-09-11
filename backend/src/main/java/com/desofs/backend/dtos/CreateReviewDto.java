package com.desofs.backend.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@AllArgsConstructor
public class CreateReviewDto {

    @NotNull(message = "Booking ID is required")
    private final String bookingId;

    @NotNull(message = "Review text is required")
    private final String text;

    @NotNull(message = "Star rating is required")
    @Min(value = 1, message = "Star rating must be at least 1")
    @Max(value = 5, message = "Star rating must be at most 5")
    private final int stars;

    @NotNull(message = "Image URL list is required")
    private final List<MultipartFile> imageUrlList;
}
