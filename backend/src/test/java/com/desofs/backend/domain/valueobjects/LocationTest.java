package com.desofs.backend.domain.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LocationTest {
    @Test
    public void testValidLocationCreation() {
        double longitude = 10.0;
        double latitude = 20.0;
        Location location = Location.create(latitude, longitude);

        assertEquals(location.getLon(), longitude);
        assertEquals(location.getLat(), latitude);
    }

    @Test
    public void testInvalidLongitude() {
        double invalidLongitude = -190.0;
        double validLatitude = 20.0;
        assertThrows(IllegalArgumentException.class, () -> Location.create(validLatitude, invalidLongitude));
    }

    @Test
    public void testInvalidLatitude() {
        double validLongitude = 10.0;
        double invalidLatitude = -100.0;
        assertThrows(IllegalArgumentException.class, () -> Location.create(invalidLatitude, validLongitude));
    }

    @Test
    public void testValidLongitudeBoundary() {
        double minLongitude = -180.0;
        double maxLongitude = 180.0;
        double validLatitude = 20.0;

        Location minLocation = Location.create(validLatitude, minLongitude);
        Location maxLocation = Location.create(validLatitude, maxLongitude);

        assertEquals(minLongitude, minLocation.getLon());
        assertEquals(maxLongitude, maxLocation.getLon());
    }

    @Test
    public void testValidLatitudeBoundary() {
        double minLatitude = -90.0;
        double maxLatitude = 90.0;
        double validLongitude = 10.0;

        Location minLocation = Location.create(minLatitude, validLongitude);
        Location maxLocation = Location.create(maxLatitude, validLongitude);

        assertEquals(minLatitude, minLocation.getLat());
        assertEquals(maxLatitude, maxLocation.getLat());
    }

}