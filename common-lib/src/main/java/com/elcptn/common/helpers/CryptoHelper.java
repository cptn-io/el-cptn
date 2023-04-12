package com.elcptn.common.helpers;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

/* @author: kc, created on 3/27/23 */
@Component
public class CryptoHelper implements InitializingBean {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH = 16;
    private static final int IV_LENGTH = 12;
    private final SecretKey secretKey;

    public CryptoHelper(@Value("${cptn.db.secret}") String hexSecret) {
        if (hexSecret != null) {
            byte[] secretBytes = HexFormat.of().parseHex(hexSecret);
            secretKey = new SecretKeySpec(secretBytes, "AES");
        } else {
            secretKey = null;
        }
    }

    public String encrypt(String plainText) throws Exception {
        SecureRandom secureRandom = new SecureRandom();
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        byte[] iv = new byte[IV_LENGTH];
        secureRandom.nextBytes(iv);

        GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH * 8, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

        byte[] cipherBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherBytes.length);
        byteBuffer.put(iv);
        byteBuffer.put(cipherBytes);
        byte[] cipherBytesWithIV = byteBuffer.array();

        return new String(Base64.getEncoder().encode(cipherBytesWithIV));
    }

    public String decrypt(String cipherText) throws Exception {
        byte[] cipherBytesWithIV = Base64.getDecoder().decode(cipherText);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH * 8, cipherBytesWithIV, 0, IV_LENGTH);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

        byte[] plainTextBytes = cipher.doFinal(cipherBytesWithIV, IV_LENGTH, cipherBytesWithIV.length - IV_LENGTH);
        return new String(plainTextBytes);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(secretKey, "DB_CRYPTO_SECRET is null");
    }
}
