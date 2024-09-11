package com.desofs.backend.services;

import com.desofs.backend.database.mappers.ReviewMapper;
import com.desofs.backend.database.repositories.BookingRepository;
import com.desofs.backend.database.repositories.ReviewRepository;
import com.desofs.backend.domain.aggregates.BookingDomain;
import com.desofs.backend.domain.aggregates.ReviewDomain;
import com.desofs.backend.domain.enums.ReviewState;
import com.desofs.backend.dtos.CreateReviewDto;
import com.desofs.backend.dtos.FetchReviewDto;
import com.desofs.backend.exceptions.DatabaseException;
import com.desofs.backend.exceptions.NotAuthorizedException;
import com.desofs.backend.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final ReviewMapper reviewMapper;
    private final LoggerService logger;

    @Transactional
    public FetchReviewDto create(CreateReviewDto createReviewDto, String userId)
            throws DatabaseException, NotAuthorizedException, IOException {

        if (createReviewDto.getText().chars().count() > 250) {
            logger.error("User with ID " + userId + " tried to add a too long review text");
            throw new IllegalArgumentException("Review text is too long");
        }

        if (!areImagesValid(createReviewDto.getImageUrlList())) {
            logger.error("User with ID " + userId + " tried to upload invalid images");
            throw new IllegalArgumentException("Images are invalid");
        }

        try {
            List<String> uuids = createReviewDto.getImageUrlList().stream()
                    .map(image -> UUID.randomUUID().toString())
                    .toList();

            ReviewDomain reviewDomain = new ReviewDomain(createReviewDto, createReviewDto.getImageUrlList(), uuids, userId);
            this.reviewRepository.create(reviewDomain, createReviewDto.getImageUrlList(), uuids, createReviewDto.getBookingId(), userId);

            logger.info("Review created by user " + userId + " for booking " + createReviewDto.getBookingId());
            return reviewMapper.domainToDto(reviewDomain);
        } catch (Exception e) {
            logger.error("Failed to create review by user " + userId);
            throw e;
        }
    }

    @Transactional
    public FetchReviewDto findById(String reviewId) throws NotFoundException {
        try {
            ReviewDomain reviewDomain = this.reviewRepository.findById(reviewId);
            if (reviewDomain == null) {
                throw new NotFoundException("Review not found");
            }

            logger.info("Review ID " + reviewId + " found");
            return reviewMapper.domainToDto(reviewDomain);
        } catch (NotFoundException e) {
            logger.warn("Review of ID " + reviewId + " not found");
            throw e;
        } catch (Exception e) {
            logger.error("Error finding review " + reviewId);
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public FetchReviewDto changeState(String reviewId, String state) throws NotFoundException, DatabaseException {
        try {
            ReviewDomain reviewDomain = this.reviewRepository.findById(reviewId);
            if (reviewDomain == null) {
                throw new NotFoundException("Review not found");
            }

            if (!EnumUtils.isValidEnum(ReviewState.class, state)) {
                throw new NotFoundException("State is not valid");
            }

            reviewDomain.changeState(ReviewState.valueOf(state));
            this.reviewRepository.update(reviewDomain);

            logger.info("Review " + reviewId + " state changed to " + state);
            return this.reviewMapper.domainToDto(reviewDomain);
        } catch (NotFoundException e) {
            logger.warn("Failed to change state review " + reviewId);
            throw e;
        } catch (DatabaseException e) {
            logger.error("Database error changing state for review " + reviewId);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error changing state for review " + reviewId);
            throw new RuntimeException(e);
        }
    }

    private boolean areImagesValid(List<MultipartFile> images) {
        if (images.size() > 3) {
            return false;
        }

        for (MultipartFile image : images) {
            if ((image.getSize() > 1024 * 1024) || !isValidImageMetadata(image)) { // 1MB
                return false;
            }
        }

        return true;
    }

    private boolean isValidImageMetadata(MultipartFile image) {
        try {
            try (ImageInputStream iis = ImageIO.createImageInputStream(image.getInputStream())) {
                Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(iis);
                if (!imageReaders.hasNext()) {
                    return false;
                }
                ImageReader reader = imageReaders.next();
                reader.setInput(iis);
                IIOMetadata metadata = reader.getImageMetadata(0);

                if (containsExecutableCode(metadata)) {
                    return false;
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean containsExecutableCode(IIOMetadata metadata) {
        String[] metadataFormats = metadata.getMetadataFormatNames();
        for (String format : metadataFormats) {
            Node rootNode = metadata.getAsTree(format);
            if (containsScriptNode(rootNode)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsScriptNode(Node node) {
        if (node.getNodeType() == Node.TEXT_NODE) {
            String textContent = node.getTextContent();
            return textContent.contains("script") || textContent.contains("<script>");
        } else {
            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                if (containsScriptNode(children.item(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    @Transactional
    public List<FetchReviewDto> getAllByRentalPropertyId(String rentalPropertyId) throws NotFoundException {
        List<BookingDomain> bookings = this.bookingRepository.findAllByRentalPropertyId(rentalPropertyId);
        List<FetchReviewDto> result = new ArrayList<>();

        for (BookingDomain booking : bookings){
            if(booking.getReview() != null){
                result.add(this.findById(booking.getReview().getId().value()));
            }
        }

        return result;
    }

    @Transactional
    public List<FetchReviewDto> getAllActiveByRentalPropertyId(String rentalPropertyId) throws NotFoundException {
        List<BookingDomain> bookings = this.bookingRepository.findAllByRentalPropertyId(rentalPropertyId);
        List<FetchReviewDto> result = new ArrayList<>();

        for (BookingDomain booking : bookings){
            if(booking.getReview() != null && booking.getReview().getState().equals(ReviewState.ACCEPTED)){
                result.add(this.findById(booking.getReview().getId().value()));
            }
        }

        return result;
    }
}
