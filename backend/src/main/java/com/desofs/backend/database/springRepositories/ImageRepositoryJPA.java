package com.desofs.backend.database.springRepositories;

import com.desofs.backend.database.models.ImageUrlDB;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ImageRepositoryJPA extends CrudRepository<ImageUrlDB, String> {

    List<ImageUrlDB> findByReviewId(String reviewId);

}
