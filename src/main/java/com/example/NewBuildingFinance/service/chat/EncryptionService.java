package com.example.NewBuildingFinance.service.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class EncryptionService {
    // Зашифрувати AES ключ для користувача (RSA)
    public String encryptAESKeyForUser(SecretKey aesKey, String userPublicKeyBase64) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(userPublicKeyBase64);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        // Явно задаємо параметри OAEP: hash = SHA-256, MGF1 з SHA-256, порожній label
        OAEPParameterSpec oaepParams = new OAEPParameterSpec(
                "SHA-256",
                "MGF1",
                new MGF1ParameterSpec("SHA-256"),
                PSource.PSpecified.DEFAULT
        );
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey, oaepParams);
        byte[] encryptedKey = cipher.doFinal(aesKey.getEncoded());
        return Base64.getEncoder().encodeToString(encryptedKey);
    }

    // Розшифрувати AES ключ для користувача (RSA)
    public SecretKey decryptAESKey(String encryptedAESKeyBase64, String userPrivateKeyBase64) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(userPrivateKeyBase64);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        // Явно задаємо параметри OAEP: hash = SHA-256, MGF1 з SHA-256, порожній label
        OAEPParameterSpec oaepParams = new OAEPParameterSpec(
                "SHA-256",
                "MGF1",
                new MGF1ParameterSpec("SHA-256"),
                PSource.PSpecified.DEFAULT
        );
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey, oaepParams);
        byte[] decryptedKeyBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedAESKeyBase64));
        return new SecretKeySpec(decryptedKeyBytes, "AES");
    }

    // Зашифрувати повідомлення за допомогою AES (AES/GCM)
    public EncryptedData encryptMessageAES(SecretKey aesKey, String message) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        byte[] iv = new byte[12];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, spec);
        byte[] ciphertext = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));

        return new EncryptedData(Base64.getEncoder().encodeToString(ciphertext),
                Base64.getEncoder().encodeToString(iv));
    }

    // Розшифрувати повідомлення за допомогою AES (AES/GCM)
    public String decryptMessageAES(SecretKey aesKey, String encryptedMessage, String ivBase64) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        byte[] iv = Base64.getDecoder().decode(ivBase64);
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, aesKey, spec);
        byte[] plaintext = cipher.doFinal(Base64.getDecoder().decode(encryptedMessage));
        return new String(plaintext, StandardCharsets.UTF_8);
    }

    public KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048); // Генеруємо ключі 2048 біт
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating RSA key pair", e);
        }
    }

    // Допоміжний клас для передачі зашифрованих даних
    public static class EncryptedData {
        private final String encryptedMessage;
        private final String iv;
        public EncryptedData(String encryptedMessage, String iv) {
            this.encryptedMessage = encryptedMessage;
            this.iv = iv;
        }
        public String getEncryptedMessage() { return encryptedMessage; }
        public String getIv() { return iv; }
    }
}
