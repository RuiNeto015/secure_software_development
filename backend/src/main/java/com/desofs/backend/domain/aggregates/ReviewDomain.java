package com.desofs.backend.domain.aggregates;

import com.desofs.backend.domain.enums.ReviewState;
import com.desofs.backend.domain.valueobjects.*;
import com.desofs.backend.dtos.CreateReviewDto;
import com.desofs.backend.utils.ListUtils;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

public class ReviewDomain {
    private final Id id;
    private final Id authorId;
    private final Id bookingId;
    private final ReviewText text;
    private final ReviewStars stars;
    @Getter
    private ReviewState state;
    private final List<ImageUrl> imageUrlList;

    // Constructors ----------------------------------------------------------------------------------------------------

    public ReviewDomain(Id id, Id authorId, Id bookingId, ReviewText text, ReviewStars stars, ReviewState state,
                        List<ImageUrl> imageUrlList) {
        notNull(id, "Id must not be null.");
        notNull(authorId, "AuthorId must not be null.");
        notNull(bookingId, "BookingId must not be null.");
        notNull(text, "Text must not be null.");
        notNull(stars, "Stars must not be null.");
        notNull(state, "State must not be null.");
        notNull(imageUrlList, "ImageUrlList must not be null.");
        isTrue(!ListUtils.hasDuplicates(imageUrlList), "ImageUrlList cannot contain duplicates.");

        this.id = id.copy();
        this.authorId = authorId.copy();
        this.bookingId = bookingId.copy();
        this.text = text.copy();
        this.stars = stars.copy();
        this.state = state;
        this.imageUrlList = List.copyOf(imageUrlList);
    }

    // Used to create a Review
    public ReviewDomain(CreateReviewDto reviewDto, List<MultipartFile> images, List<String> uuids, String authorId) {
        this(
                Id.create(UUID.randomUUID().toString()),
                Id.create(authorId),
                Id.create(reviewDto.getBookingId()),
                ReviewText.create(reviewDto.getText()),
                ReviewStars.create(reviewDto.getStars()),
                ReviewState.PENDING,
                createImageUrls(images, uuids));
    }

    private static List<ImageUrl> createImageUrls(List<MultipartFile> images, List<String> uuids) {
        List<ImageUrl> imageUrls = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            MultipartFile image = images.get(i);
            String uuid = uuids.get(i);
            ImageUrl imageUrl = ImageUrl.create(
                    Id.create(uuid),
                    "https://localhost:8080/images/" + uuid + "_" + image.getOriginalFilename()
            );
            imageUrls.add(imageUrl);
        }

        return imageUrls;
    }

    // Getters ---------------------------------------------------------------------------------------------------------

    public Id getId() {
        return id.copy();
    }

    public Id getAuthorId() {
        return authorId.copy();
    }

    public Id getBookingId() {
        return bookingId.copy();
    }

    public ReviewText getText() {
        return text.copy();
    }

    public ReviewStars getStars() {
        return stars.copy();
    }

    public List<ImageUrl> getImageUrlList() {
        return List.copyOf(imageUrlList);
    }

    public ReviewDomain copy() {
        return new ReviewDomain(id.copy(), authorId.copy(), bookingId.copy(), text.copy(),
                stars.copy(), state, List.copyOf(imageUrlList));
    }

    // Domain methods --------------------------------------------------------------------------------------------------

    public void changeState(ReviewState state) {
        if (state == ReviewState.DELETED) {
            throw new IllegalArgumentException("The review is already deleted");
        } else if (!Objects.equals(this.state.toString(), ReviewState.PENDING.toString())) {
            throw new IllegalArgumentException("The review is already responded");
        } else if (state == ReviewState.PENDING) {
            throw new IllegalArgumentException("The review is already waiting for response");
        }
        this.state = state;
    }
}
