package com.desofs.backend.controllers;

import com.desofs.backend.domain.enums.Authority;
import com.desofs.backend.dtos.FetchUserDto;
import com.desofs.backend.dtos.UpdateRentalPropertyDto;
import com.desofs.backend.exceptions.DatabaseException;
import com.desofs.backend.dtos.CreateRentalPropertyDto;
import com.desofs.backend.dtos.FetchRentalPropertyDto;
import com.desofs.backend.exceptions.NotAuthorizedException;
import com.desofs.backend.exceptions.NotFoundException;
import com.desofs.backend.services.RentalPropertyService;
import com.desofs.backend.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.desofs.backend.config.UserDetailsConfig.hasAuthorization;

@Tag(name = "Rental Property", description = "Endpoints for managing rental properties")
@RestController
@RequestMapping(path = "/rental_property")
@RequiredArgsConstructor
public class RentalPropertyController {

    private final RentalPropertyService rentalPropertyService;
    private final UserService userService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<FetchRentalPropertyDto> create(@RequestBody final CreateRentalPropertyDto createRentalPropertyDto,
                                                         final Authentication authentication)
            throws DatabaseException {
        String userId = authentication.getName();
        FetchRentalPropertyDto rentalProperty = this.rentalPropertyService.create(createRentalPropertyDto, userId);
        return new ResponseEntity<>(rentalProperty, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FetchRentalPropertyDto> getById(@PathVariable final String id) throws NotFoundException {
        FetchRentalPropertyDto rentalProperty = this.rentalPropertyService.findById(id);
        return ResponseEntity.ok().body(rentalProperty);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<FetchRentalPropertyDto>> getAll() {
        return new ResponseEntity<>(this.rentalPropertyService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/allByUser/{id}")
    public ResponseEntity<List<FetchRentalPropertyDto>> getAllByUser(@PathVariable final String id)
            throws NotFoundException {

        FetchUserDto user = userService.findById(id);
        if (user == null) {
            throw new NotFoundException("User not found.");
        }

        List<FetchRentalPropertyDto> rentalProperty = this.rentalPropertyService.findAllByUserId(id);
        return ResponseEntity.ok().body(rentalProperty);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<FetchRentalPropertyDto> update(@PathVariable final String id,
                                                         @RequestBody final UpdateRentalPropertyDto updateRentalPropertyDto,
                                                         final Authentication authentication)
            throws DatabaseException, NotFoundException, NotAuthorizedException {

        String userId = authentication.getName();
        FetchRentalPropertyDto rentalProperty = this.rentalPropertyService.findById(id);

        if (!rentalProperty.getPropertyOwner().equals(userId) && !hasAuthorization(authentication, Authority.BUSINESSADMIN)) {
            throw new NotAuthorizedException("The request user is not the owner of rental property.");
        }

        return new ResponseEntity<>(this.rentalPropertyService.update(id, updateRentalPropertyDto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<FetchRentalPropertyDto> delete(@PathVariable final String id,
                                                         final Authentication authentication)
            throws NotAuthorizedException, DatabaseException, NotFoundException {

        String userId = authentication.getName();
        FetchRentalPropertyDto rentalProperty = this.rentalPropertyService.findById(id);

        if (!rentalProperty.getPropertyOwner().equals(userId) && !hasAuthorization(authentication, Authority.BUSINESSADMIN)) {
            throw new NotAuthorizedException("The request user is not the owner of rental property.");
        }

        return new ResponseEntity<>(this.rentalPropertyService.deactivate(id), HttpStatus.OK);
    }
}
