package com.desofs.backend.bootstrapper;

import com.desofs.backend.database.repositories.RentalPropertyRepository;
import com.desofs.backend.domain.aggregates.RentalPropertyDomain;
import com.desofs.backend.domain.valueobjects.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RentalPropertyBootstrapper {

    private final RentalPropertyRepository rentalPropertyRepository;

    public void run() throws Exception {
        String userId = "1a1a1a1a-1a1a-1a1a-1a1a-1a1a1a1a1a1a";

        RentalPropertyDomain rentalProperty1 = new RentalPropertyDomain(
                Id.create(UUID.randomUUID().toString()),
                Id.create(userId),
                PropertyName.create("Property 1"),
                Location.create(40.712776, -74.005974),
                PositiveInteger.create(5),
                PositiveInteger.create(3),
                PositiveInteger.create(2),
                PropertyDescription.create("Property 1 description"),
                MoneyAmount.create(new BigDecimal("200.00")),
                new ArrayList<>(),
                new ArrayList<>(),
                true
        );
        this.rentalPropertyRepository.create(rentalProperty1);

        RentalPropertyDomain rentalProperty2 = new RentalPropertyDomain(
                Id.create(UUID.randomUUID().toString()),
                Id.create(userId),
                PropertyName.create("Property 2"),
                Location.create(34.052235, -118.243683),
                PositiveInteger.create(4),
                PositiveInteger.create(2),
                PositiveInteger.create(1),
                PropertyDescription.create("Property 2 description"),
                MoneyAmount.create(new BigDecimal("150.00")),
                new ArrayList<>(),
                new ArrayList<>(),
                true
        );
        this.rentalPropertyRepository.create(rentalProperty2);

        RentalPropertyDomain rentalProperty3 = new RentalPropertyDomain(
                Id.create(UUID.randomUUID().toString()),
                Id.create(userId),
                PropertyName.create("Property 3"),
                Location.create(51.507351, -0.127758),
                PositiveInteger.create(6),
                PositiveInteger.create(3),
                PositiveInteger.create(2),
                PropertyDescription.create("Property 3 description"),
                MoneyAmount.create(new BigDecimal("250.00")),
                new ArrayList<>(),
                new ArrayList<>(),
                true
        );
        this.rentalPropertyRepository.create(rentalProperty3);
    }
}
