package com.desofs.backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Date;

@Getter
public class IntervalTimeDto {
    private final Date from;
    private final Date to;

    public IntervalTimeDto(@JsonProperty("from") Date from, @JsonProperty("to") Date to) {
        this.from = from;
        this.to = to;
    }
}
