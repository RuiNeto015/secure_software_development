package com.desofs.backend.database.mappers;

import com.desofs.backend.database.models.ImageUrlDB;
import com.desofs.backend.database.models.ReviewDB;
import com.desofs.backend.domain.aggregates.ReviewDomain;
import com.desofs.backend.domain.enums.ReviewState;
import com.desofs.backend.domain.valueobjects.Id;
import com.desofs.backend.domain.valueobjects.ImageUrl;
import com.desofs.backend.domain.valueobjects.ReviewStars;
import com.desofs.backend.domain.valueobjects.ReviewText;
import com.desofs.backend.dtos.FetchReviewDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReviewMapper {

    public FetchReviewDto domainToDto(ReviewDomain review) {
        return new FetchReviewDto(
                review.getId().value(),
                review.getAuthorId().value(),
                review.getBookingId().value(),
                review.getText().value(),
                review.getStars().getStars(),
                review.getState().toString(),
                review.getImageUrlList().stream().map(ImageUrl::getUrl).toList());
    }

    public ReviewDB domainToDb(ReviewDomain review) {
        return new ReviewDB(
                review.getId().value(),
                review.getAuthorId().value(),
                review.getBookingId().value(),
                review.getText().value(),
                review.getStars().getStars(),
                review.getState().toString());
    }

    public ReviewDomain dbToDomain(ReviewDB review, List<ImageUrlDB> imageUrls) {
        return new ReviewDomain(
                Id.create(review.getId()),
                Id.create(review.getUserId()),
                Id.create(review.getBookingId()),
                ReviewText.create(review.getText()),
                ReviewStars.create(review.getStars()),
                ReviewState.valueOf(review.getState()),
                imageUrls.stream().map(i -> ImageUrl.create(Id.create(i.getId()), i.getReference())).toList());
    }
}