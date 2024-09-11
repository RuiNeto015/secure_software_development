package com.desofs.backend.services;

import com.desofs.backend.domain.valueobjects.PhoneNumber;
import com.desofs.backend.dtos.*;
import com.desofs.backend.exceptions.DatabaseException;
import com.desofs.backend.database.repositories.UserRepository;
import com.desofs.backend.domain.aggregates.UserDomain;
import com.desofs.backend.domain.valueobjects.Email;
import com.desofs.backend.domain.valueobjects.Name;
import com.desofs.backend.domain.valueobjects.Password;
import com.desofs.backend.exceptions.NotFoundException;
import com.desofs.backend.exceptions.ResetPasswordExpiredToken;
import com.desofs.backend.exceptions.UpdatePasswordException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mailersend.sdk.MailerSend;
import com.mailersend.sdk.exceptions.MailerSendException;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final LoggerService logger;
    private final EncryptionService encryptionService;
    private final PwnedPasswordChecker passwordChecker;

    @Value("${mailSenderApiKey}")
    private String mailSenderApiKey;

    @Transactional
    public FetchUserDto create(CreateUserDto createUserDTO) throws DatabaseException {
        UserDomain user = new UserDomain(Name.create(createUserDTO.getName()), Email.create(createUserDTO.getEmail()),
                Password.create(createUserDTO.getPassword()), PhoneNumber.create(createUserDTO.getPhoneNumber()),
                createUserDTO.getRole());

        UserDomain userToCreate = new UserDomain(user.getId(), user.getName(), user.getEmail(),
                Password.create(encoder.encode(user.getPassword().value())), user.getPhoneNumber(), user.getRole(),
                user.isBanned());

        this.userRepository.create(userToCreate);
        logger.info("User " + createUserDTO.getEmail() + " registered");
        return new FetchUserDto(user.getId().value(), user.getName().value(), user.getEmail().value(),
                user.getPhoneNumber().value(), user.getRole(), user.isBanned());
    }

    @Transactional
    public FetchUserDto findByEmail(String email) {
        UserDomain user = this.userRepository.findByEmail(email);
        if (user != null) {
            return new FetchUserDto(user.getId().value(), user.getName().value(), user.getEmail().value(),
                    user.getPhoneNumber().value(), user.getRole(), user.isBanned());
        } else {
            return null;
        }
    }

    @Transactional
    public FetchUserDto findById(String id) {
        UserDomain user = this.userRepository.findById(id);
        if (user != null) {
            return new FetchUserDto(user.getId().value(), user.getName().value(), user.getEmail().value(),
                    user.getPhoneNumber().value(), user.getRole(), user.isBanned());
        } else {
            return null;
        }
    }

    private void updateUserPassword(UserDomain user, String newPassword) throws MailerSendException {
        if (passwordChecker.passwordHasBeenPwned(newPassword)) {
            throw new IllegalArgumentException("The password it was found on the pwned database!");
        }

        UserDomain updatedUser = new UserDomain(user.getId(), user.getName(), user.getEmail(),
                Password.create(encoder.encode(newPassword)), user.getPhoneNumber(), user.getRole(),
                user.isBanned());

        this.userRepository.update(updatedUser);
        com.mailersend.sdk.emails.Email email = new com.mailersend.sdk.emails.Email();
        email.subject = "TripNau - Your password has been updated";
        email.text = "Your password has been updated.";
        email.addRecipient(updatedUser.getName().value(), updatedUser.getEmail().value());
        email.setFrom("desofs-TripNau", "desofs@trial-0p7kx4x85qmg9yjr.mlsender.net");

        MailerSend ms = new MailerSend();
        ms.setToken(mailSenderApiKey);
        ms.emails().send(email);
    }

    @Transactional
    public void updatePassword(UpdatePasswordDto updatePasswordDto, String userId) throws MailerSendException,
            UpdatePasswordException {

        UserDomain user = this.userRepository.findById(userId);

        if (!encoder.matches(updatePasswordDto.oldPassword(), user.getPassword().value())) {
            throw new UpdatePasswordException("current password is wrong");
        }

        this.updateUserPassword(user, updatePasswordDto.newPassword());
    }

    public void forgotPassword(ForgotPasswordDto forgotPasswordDto) throws Exception {
        FetchUserDto user = this.findByEmail(forgotPasswordDto.email());

        if (user == null) {
            throw new NotFoundException("provided email does not exist");
        }

        ForgotPasswordMetaDto meta = new ForgotPasswordMetaDto(forgotPasswordDto.email(),
                new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5)));

        ObjectMapper objectMapper = new ObjectMapper();
        String encryptedMetadata = encryptionService.encryptMessage(objectMapper.writeValueAsString(meta));
        com.mailersend.sdk.emails.Email email = new com.mailersend.sdk.emails.Email();
        email.subject = "TripNau - Request to reset password";
        email.text = "Press this link to reset your password " + encryptedMetadata;
        email.addRecipient(user.getName(), user.getEmail());
        email.setFrom("desofs-TripNau", "desofs@trial-0p7kx4x85qmg9yjr.mlsender.net");

        MailerSend ms = new MailerSend();
        ms.setToken(mailSenderApiKey);
        ms.emails().send(email);
    }

    @Transactional
    public void resetPassword(ForgotPasswordNewDto forgotPasswordNewDto) throws Exception {
        String decryptedMedataAsString = encryptionService.decryptMessage(forgotPasswordNewDto.token());
        ObjectMapper objectMapper = new ObjectMapper();
        ForgotPasswordMetaDto metadata = objectMapper.readValue(decryptedMedataAsString, ForgotPasswordMetaDto.class);

        if (metadata.expiryDate().before(new Date())) {
            throw new ResetPasswordExpiredToken();
        }

        UserDomain userDomain = this.userRepository.findByEmail(metadata.email());
        this.updateUserPassword(userDomain, forgotPasswordNewDto.newPassword());
    }

    public boolean isPasswordValid(String email, String password) {
        UserDomain user = this.userRepository.findByEmail(email);
        return encoder.matches(password, user.getPassword().value());
    }
}

