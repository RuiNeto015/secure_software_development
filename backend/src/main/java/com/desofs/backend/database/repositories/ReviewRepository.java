package com.desofs.backend.database.repositories;

import com.desofs.backend.database.mappers.ImageUrlMapper;
import com.desofs.backend.database.mappers.ReviewMapper;
import com.desofs.backend.database.models.BookingDB;
import com.desofs.backend.database.models.ImageUrlDB;
import com.desofs.backend.database.models.ReviewDB;
import com.desofs.backend.database.springRepositories.BookingRepositoryJPA;
import com.desofs.backend.database.springRepositories.ImageRepositoryJPA;
import com.desofs.backend.database.springRepositories.ReviewRepositoryJPA;
import com.desofs.backend.domain.aggregates.ReviewDomain;
import com.desofs.backend.domain.valueobjects.Id;
import com.desofs.backend.domain.valueobjects.ImageUrl;
import com.desofs.backend.exceptions.DatabaseException;
import com.desofs.backend.exceptions.NotAuthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component("ReviewRepositoryCapsule")
@RequiredArgsConstructor
public class ReviewRepository {

    private final ReviewRepositoryJPA reviewRepositoryJPA;

    private final BookingRepositoryJPA bookingRepositoryJPA;

    private final ImageRepositoryJPA imageRepositoryJPA;

    private final ReviewMapper reviewMapper;

    private final ImageUrlMapper imageUrlMapper;

    public void create(ReviewDomain reviewDomain, List<MultipartFile> images, List<String> uuids, String bookingId,
                       String userId) throws DatabaseException, NotAuthorizedException, IOException {

        ReviewDB reviewDB = this.reviewRepositoryJPA.findById(reviewDomain.getId().value()).orElse(null);

        if (reviewDB != null) {
            throw new DatabaseException("Duplicated ID violation.");
        }

        BookingDB bookingDB = this.bookingRepositoryJPA.findById(bookingId).orElse(null);

        if (bookingDB == null) {
            throw new DatabaseException("Booking not found.");
        }

        if (!Objects.equals(bookingDB.getAccountId(), userId)) {
            throw new NotAuthorizedException("You are trying to review a booking that is not yours.");
        }

        this.reviewRepositoryJPA.save(this.reviewMapper.domainToDb(reviewDomain));

        String uploadDirectory = "src/main/resources/static/images";

        for (int i = 0; i < images.size(); i++) {
            String uuid = uuids.get(i);
            saveImageToStorage(uploadDirectory, images.get(i), UUID.fromString(uuid));

            this.imageRepositoryJPA.save(imageUrlMapper.domainToDb(
                    ImageUrl.create(
                            Id.create(uuid),
                            "https://localhost:8080/images/" + uuid + "_" + images.get(i).getOriginalFilename()),
                    reviewDomain.getId().value()));
        }
    }

    private void saveImageToStorage(String uploadDirectory, MultipartFile imageFile, UUID uuid) throws IOException {
        String uniqueFileName = uuid + "_" + imageFile.getOriginalFilename();

        Path uploadPath = Path.of(uploadDirectory);
        Path filePath = uploadPath.resolve(uniqueFileName);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

    }

    public ReviewDomain findById(String reviewId) {
        try {
            ReviewDB reviewDB = this.reviewRepositoryJPA.findById(reviewId).orElse(null);
            if (reviewDB != null) {
                List<ImageUrlDB> imageUrlDB = imageRepositoryJPA.findByReviewId(reviewId);
                return this.reviewMapper.dbToDomain(reviewDB, imageUrlDB);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public ReviewDomain findByBookingId(String bookingId) {
        ReviewDB reviewDB = this.reviewRepositoryJPA.findByBookingId(bookingId);
        return this.findById(reviewDB.getId());
    }

    public void update(ReviewDomain reviewDomain) throws DatabaseException {
        ReviewDB reviewDB = this.reviewRepositoryJPA.findById(reviewDomain.getId().value()).orElse(null);

        if (reviewDB == null) {
            throw new DatabaseException("Review not found");
        }

        this.reviewRepositoryJPA.save(this.reviewMapper.domainToDb(reviewDomain));
    }
}
