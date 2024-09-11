package com.desofs.backend.bootstrapper;

import com.desofs.backend.database.repositories.UserRepository;
import com.desofs.backend.domain.aggregates.UserDomain;
import com.desofs.backend.domain.enums.Authority;
import com.desofs.backend.domain.valueobjects.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserBootstrapper {

    private final UserRepository userRepository;

    private final PasswordEncoder encoder;

    public void run() throws Exception {

        String palavraChave = "6a8eE83IV0vdvkC";

        UserDomain businessAdmin = new UserDomain(Id.create(UUID.randomUUID().toString()), Name.create("Business Admin"),
                Email.create("businessadmin@mail.com"), Password.create(encoder.encode(palavraChave)),
                PhoneNumber.create("910123123"), Authority.BUSINESSADMIN, false);
        if (this.userRepository.findByEmail("businessadmin@mail.com") == null) {
            this.userRepository.create(businessAdmin);
        }

        UserDomain propertyOwner = new UserDomain(Id.create("1a1a1a1a-1a1a-1a1a-1a1a-1a1a1a1a1a1a"), Name.create("Property Owner"),
                Email.create("propertyowner@mail.com"), Password.create(encoder.encode(palavraChave)),
                PhoneNumber.create("911123123"), Authority.PROPERTYOWNER, false);
        if (this.userRepository.findByEmail("propertyowner@mail.com") == null) {
            this.userRepository.create(propertyOwner);
        }

        UserDomain customer = new UserDomain(Id.create(UUID.randomUUID().toString()), Name.create("Customer"),
                Email.create("customer@mail.com"), Password.create(encoder.encode(palavraChave)),
                PhoneNumber.create("912123123"), Authority.CUSTOMER, false);
        if (this.userRepository.findByEmail("customer@mail.com") == null) {
            this.userRepository.create(customer);
        }
    }
}
