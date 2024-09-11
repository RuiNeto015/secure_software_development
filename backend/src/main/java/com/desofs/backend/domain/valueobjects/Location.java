package com.desofs.backend.domain.valueobjects;

import lombok.Getter;

import static com.desofs.backend.utils.LocationUtils.isValidLatitude;
import static com.desofs.backend.utils.LocationUtils.isValidLongitude;
import static org.apache.commons.lang3.Validate.isTrue;

@Getter
public class Location {

    private final double lat;
    private final double lon;

    private Location(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public static Location create(double lat, double lon) {
        isTrue(isValidLatitude(lat),
                "Invalid latitude value.");
        isTrue(isValidLongitude(lon),
                "Invalid longitude value.");
        return new Location(lat, lon);
    }

    public Location copy() {
        return new Location(lat, lon);
    }

}
