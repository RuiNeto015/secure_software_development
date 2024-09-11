package com.desofs.backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
@RequiredArgsConstructor
public class PwnedPasswordChecker {

    private final LoggerService logger;

    private static final String HASH_FILE_PATH = "src/main/resources/hibp_hashes.txt";

    public boolean passwordHasBeenPwned(String password) {
        try {
            String hashedPassword = hashPasswordSHA1(password);

            String prefix = hashedPassword.substring(0, 5);
            String suffix = hashedPassword.substring(5).toUpperCase();

            // Read the hash file and look for matching entries
            try (BufferedReader reader = new BufferedReader(new FileReader(HASH_FILE_PATH))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith(prefix)) {
                        String[] parts = line.split(":");
                        String fileSuffix = parts[0].substring(5);
                        if (fileSuffix.equals(suffix)) {
                            logger.warn("Password has been pwned");
                            return true;
                        }
                    }
                }
            }
        } catch (NoSuchAlgorithmException | IOException e) {
            logger.warn("Error in the passwordHasBeenPwned");
        }
        return false;
    }

    private String hashPasswordSHA1(String password) throws NoSuchAlgorithmException {
        // BEGIN-NOSCAN
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        // END-NOSCAN
        byte[] hashBytes = sha1.digest(password.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString().toUpperCase();
    }

}
