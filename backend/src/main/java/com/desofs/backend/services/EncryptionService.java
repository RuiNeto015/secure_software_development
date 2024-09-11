package com.desofs.backend.services;

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
        publicKeyString = publicKeyString.replace("\n", "");
        publicKeyString = publicKeyString.replace("-----END PUBLIC KEY-----", "");
        publicKeyString = publicKeyString.replace("-----BEGIN PUBLIC KEY-----", "");

        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    private String signMessage(String message) throws Exception {
        Signature signature = Signature.getInstance("SHA512withRSA");
        PrivateKey privateKey = parsePrivateKey(this.privateKey);
        signature.initSign(privateKey);
        signature.update(message.getBytes());
        byte[] signBytes = signature.sign();
        return Base64.getEncoder().encodeToString(signBytes);
    }

    private PrivateKey parsePrivateKey(String privateKeyString) throws Exception {
        privateKeyString = privateKeyString.replace("\n", "");
        privateKeyString = privateKeyString.replace("-----BEGIN PRIVATE KEY-----", "");
        privateKeyString = privateKeyString.replace("-----END PRIVATE KEY-----", "");

        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    public String encryptMessage(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-512ANDMGF1PADDING");
        PublicKey publicKey = parsePublicKey(this.publicKey);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encrypt = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encrypt);
    }

    public String decryptMessage(String encryptedMessage) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-512ANDMGF1PADDING");
        PrivateKey privateKey = parsePrivateKey(this.privateKey);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decrypt = cipher.doFinal(Base64.getDecoder().decode(encryptedMessage));
        return new String(decrypt);
    }

    public String encryptAndSignMessage(String plainText) throws Exception {
        String signature = signMessage(plainText);
        String encryptedMessage = encryptMessage(plainText);
        return encryptedMessage + "::::" + signature;
    }

}
