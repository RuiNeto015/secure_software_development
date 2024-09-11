package com.desofs.backend.controllers;

import com.desofs.backend.domain.enums.Authority;
import com.desofs.backend.dtos.*;
import com.desofs.backend.exceptions.DatabaseException;
import com.desofs.backend.exceptions.NotAuthorizedException;
import com.desofs.backend.exceptions.NotFoundException;
import com.desofs.backend.exceptions.UpdatePasswordException;
import com.desofs.backend.services.JwtService;
import com.desofs.backend.services.LoggerService;
import com.desofs.backend.services.PwnedPasswordChecker;
import com.desofs.backend.services.UserService;
import com.mailersend.sdk.exceptions.MailerSendException;
import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.desofs.backend.config.UserDetailsConfig.hasAuthorization;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

@RestController
@RequestMapping(path = "/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final UserService userService;
    private final LoggerService logger;
    private final PwnedPasswordChecker passwordChecker;
    private final JwtService jwtService;
    private final PasswordEncoder encoder;

    @Value("${twilio.account_sid}")
    private String twilio_account_sid;

    @Value("${twilio.auth_token}")
    private String twilio_auth_token;

    @Value("${twilio.service_sid}")
    private String twilio_service_sid;

    @Value("${jwt.exp-business-admin}")
    private Long expBusinessAdmin;

    @Value("${jwt.exp-property-owner}")
    private Long expPropertyOwner;

    @Value("${jwt.exp-customer}")
    private Long expCustomer;

    @PostMapping(value = "/login/generateOTP")
    public ResponseEntity<?> generateOTP(@RequestBody @Valid final AuthRequestDto request)
            throws NotFoundException {

        FetchUserDto user = userService.findByEmail(request.getEmail());

        if (user == null) {
            logger.info("Error generating OTP: user not found");
            throw new NotFoundException("User not found");
        }

        if (!userService.isPasswordValid(request.getEmail(), request.getPassword())) {
            logger.info("Error generating OTP for " + user.getEmail() + ". Password is invalid");
            throw new IllegalArgumentException("current password is wrong");
        }

        /*Twilio.init(twilio_account_sid, twilio_auth_token);


        Verification.creator(
                        twilio_service_sid,
                        "+351" + user.getPhoneNumber(),
                        "sms")
                .create();*/

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "The OTP has been sent to the phone number");
        logger.info("OTP sent to user " + user.getEmail());
        return ResponseEntity.ok().body(responseBody);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody @Valid final LoginRequestDto request) throws BadCredentialsException {

        try {
            /*Twilio.init(twilio_account_sid, twilio_auth_token);

            try {
                VerificationCheck verificationCheck = VerificationCheck.creator(
                                twilio_service_sid)
                        .setTo("+351" + request.getPhoneNumber())
                        .setCode(request.getCode())
                        .create();

                if (!Objects.equals(verificationCheck.getStatus(), "approved")) {
                    logger.warn("OTP code (" + request.getCode() + ") received by user " + request.getEmail() + " is invalid");
                    throw new IllegalArgumentException("Wrong code");
                }

            } catch (Exception e) {
                logger.warn("Verification failed for user " + request.getEmail());
                throw new IllegalArgumentException("Verification failed");
            }*/

            final Authentication authentication = this.authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            User user = (User) authentication.getPrincipal();

            List<String> stringAuthorities = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority).toList();

            final long expiry;

            if (stringAuthorities.contains(Authority.BUSINESSADMIN)) {
                expiry = expBusinessAdmin;
            } else if (stringAuthorities.contains(Authority.PROPERTYOWNER)) {
                expiry = expPropertyOwner;
            } else {
                expiry = expCustomer;
            }

            final String scope = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                    .collect(joining(" "));

            final JwtClaimsSet claims = JwtClaimsSet.builder()
                    .expiresAt(Instant.now().plusSeconds(expiry))
                    .subject(format("%s", user.getUsername()))
                    .claim("roles", scope)
                    .claim("email", request.getEmail())
                    .build();

            final String token = this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
            jwtService.addToken(token);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("token", token);
            logger.info("User logged in: " + user.getUsername());

            return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, token).body(responseBody);
        } catch (BadCredentialsException ex) {
            logger.error("Login failed for email: " + request.getEmail());
            throw ex;
        }
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<FetchUserDto> register(@RequestBody CreateUserDto createUserDto,
                                                 Authentication authentication)
            throws DatabaseException, NotAuthorizedException {

        if (authentication != null && createUserDto.getRole().equals(Authority.BUSINESSADMIN) &&
                !hasAuthorization(authentication, Authority.BUSINESSADMIN)) {
            throw new NotAuthorizedException("You're not authorized!");
        }
        try {
            if (authentication != null
                    && createUserDto.getRole().equals(Authority.BUSINESSADMIN)
                    && !hasAuthorization(authentication, Authority.BUSINESSADMIN)) {
                logger.error("User " + authentication.getName() + " not authorized to create a business admin");
                throw new NotAuthorizedException("You're not authorized!");
            }

            if (passwordChecker.passwordHasBeenPwned(createUserDto.getPassword())) {
                throw new IllegalArgumentException("The password it was found on the pwned database! We cannot accept that ...");
            }

            FetchUserDto user = this.userService.create(createUserDto);
            logger.info("User registered: " + user.getEmail());

            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (DatabaseException | NotAuthorizedException e) {
            logger.error("Error occurred while registering user");
            throw e;
        }
    }

    @PostMapping("/update-password")
    public ResponseEntity<Void> updatePassword(@RequestBody UpdatePasswordDto updatePasswordDto, Authentication authentication)
            throws MailerSendException, UpdatePasswordException {

        this.userService.updatePassword(updatePasswordDto, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordDto forgotPasswordDto) throws Exception {
        this.userService.forgotPassword(forgotPasswordDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody ForgotPasswordNewDto forgotPasswordNewDto) throws Exception {
        this.userService.resetPassword(forgotPasswordNewDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        jwtService.removeToken(token.substring(7));
        return ResponseEntity.ok().build();
    }
}