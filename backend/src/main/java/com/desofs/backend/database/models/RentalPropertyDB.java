package com.desofs.backend.database.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity(name = "rental_property")
@Getter
public class RentalPropertyDB {

    @Id
    private String id;

    @Column(nullable = false)
    private String priceNightDefault;

    @Column(nullable = false)
    private String propertyOwnerId;

    @Column(nullable = false)
    private String propertyName;

    @Column(nullable = false)
    private double lat;

    @Column(nullable = false)
    private double lon;

    @Column(nullable = false)
    private int maxGuests;

    @Column(nullable = false)
    private int numBedrooms;

    @Column(nullable = false)
    private int numBathrooms;

    @Column(nullable = false)
    private String propertyDescription;

    @Column(nullable = false)
    private boolean isActive;

    public RentalPropertyDB(){
    }

    public RentalPropertyDB(String id, String priceNightDefault, String propertyOwnerId, String propertyName,
                            double lat, double lon, int maxGuests, int numBedrooms, int numBathrooms,
                            String propertyDescription, boolean isActive) {
        this.id = id;
        this.priceNightDefault = priceNightDefault;
        this.propertyOwnerId = propertyOwnerId;
        this.propertyName = propertyName;
        this.lat = lat;
        this.lon = lon;
        this.maxGuests = maxGuests;
        this.numBedrooms = numBedrooms;
        this.numBathrooms = numBathrooms;
        this.propertyDescription = propertyDescription;
        this.isActive = isActive;
    }
}
