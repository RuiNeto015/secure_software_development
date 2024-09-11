package com.desofs.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FetchReviewDto {

    private final String id;

    private final String authorId;

    private final String bookingId;

    private final String text;

    private final int stars;

    private final String state;

    private final List<String> imageUrlList;
}
