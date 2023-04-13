package com.elcptn.common.helpers;

import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/* @author: kc, created on 3/27/23 */
@Component
public class CryptoHelper implements InitializingBean {


    private final BytesEncryptor encryptor;

    public CryptoHelper(@Value("${cptn.crypto.secret}") String password, @Value("${cptn.crypto.salt}") String salt) {

        if (password != null && salt != null) {
            String hexSalt = HexUtils.toHexString(salt.getBytes(StandardCharsets.UTF_8));
            encryptor = Encryptors.stronger(password, hexSalt);
        } else {
            encryptor = null;
        }
    }


    public String encrypt(String plainText) {

        byte[] cipherBytes = encryptor.encrypt(plainText.getBytes(StandardCharsets.UTF_8));
        return new String(Base64.getEncoder().encode(cipherBytes));
    }

    public String decrypt(String cipherText) {
        byte[] cipherBytes = Base64.getDecoder().decode(cipherText);
        byte[] plainTextBytes = encryptor.decrypt(cipherBytes);
        return new String(plainTextBytes);
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(encryptor, "CPTN_CRYPTO_SECRET and CPTN_CRYPTO_SALT are required to be set in the environment.");
    }
}
