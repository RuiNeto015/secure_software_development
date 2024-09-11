package com.desofs.logging.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class EncryptionService {

    @Value("${logs.private-key}")
    private String privateKey;

    @Value("${logs.public-key}")
    private String publicKey;

    private PublicKey parsePublicKey(String publicKeyString) throws Exception {
        publicKeyString = publicKeyString.replace("\n", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replace("-----BEGIN PUBLIC KEY-----", "");

        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    private PrivateKey parsePrivateKey(String privateKeyString) throws Exception {
        privateKeyString = privateKeyString.replace("\n", "")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "");

        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    private String decryptMessage(String encryptedMessage) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-512ANDMGF1PADDING");
        PrivateKey privateKey = parsePrivateKey(this.privateKey);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decrypt = cipher.doFinal(Base64.getDecoder().decode(encryptedMessage));
        return new String(decrypt);
    }

    private boolean verifyMessage(String plainText, String signature) throws Exception {
        Signature publicSignature = Signature.getInstance("SHA512withRSA");
        PublicKey publicKey = parsePublicKey(this.publicKey);
        publicSignature.initVerify(publicKey);
        publicSignature.update(plainText.getBytes());
        byte[] signatureBytes = Base64.getDecoder().decode(signature);
        return publicSignature.verify(signatureBytes);
    }

    public String verifyAndDecrypt(String encryptedMessageWithSignature) throws Exception {
        String[] parts = encryptedMessageWithSignature.split("::::");
        if (parts.length != 2) {
            throw new RuntimeException("Invalid message format");
        }
        String encryptedMessage = parts[0];
        String signature = parts[1];

        String decryptedMessage = decryptMessage(encryptedMessage);

        if (!verifyMessage(decryptedMessage, signature)) {
            throw new RuntimeException("Signature verification failed");
        }

        return decryptedMessage;
    }
}
