package com.desofs.backend.utils;

import java.time.LocalDateTime;

public class LocalDateTimeUtils {

    public static LocalDateTime copyLocalDateTime(LocalDateTime originalDateTime) {
        return LocalDateTime.of(
                originalDateTime.getYear(),
                originalDateTime.getMonth(),
                originalDateTime.getDayOfMonth(),
                originalDateTime.getHour(),
                originalDateTime.getMinute(),
                originalDateTime.getSecond(),
                originalDateTime.getNano()
        );
    }



}