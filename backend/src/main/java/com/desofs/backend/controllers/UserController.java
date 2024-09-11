package com.desofs.backend.controllers;

import com.desofs.backend.dtos.FetchUserDto;
import com.desofs.backend.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "Endpoints for managing users")
@RestController
@RequestMapping(path = "/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userEmail}")
    public ResponseEntity<FetchUserDto> getUserByEmail(@PathVariable final String userEmail) {
        FetchUserDto user = this.userService.findByEmail(userEmail);
        return ResponseEntity.ok().body(user);
    }
}