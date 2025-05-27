package com.gelatoflow.gelatoflow_api.service;

import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

@Component
public class PasswordUtil {
    public static String hashPassword(String passwordToHash, String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 10000;
        char[] passwordChars = passwordToHash.toCharArray();
        byte[] saltBytes = Base64.getDecoder().decode(salt);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        PBEKeySpec spec = new PBEKeySpec(passwordChars, saltBytes, iterations, 512);
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(hash);
    }

    public static String generateSalt() {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }


}