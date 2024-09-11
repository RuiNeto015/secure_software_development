package com.desofs.backend.database.mappers;

import com.desofs.backend.database.models.ImageUrlDB;
import com.desofs.backend.domain.valueobjects.ImageUrl;
import org.springframework.stereotype.Component;

@Component
public class ImageUrlMapper {

    public ImageUrlDB domainToDb(ImageUrl image, String reviewId) {
        return new ImageUrlDB(image.getId().value(), reviewId, image.getUrl());
    }
}
