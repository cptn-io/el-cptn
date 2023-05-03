package io.cptn.common.helpers;

import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/* @author: kc, created on 3/27/23 */
@Component
public class CryptoHelper implements InitializingBean {

    private static final int SALT_LENGTH = 16;

    @Value("${cptn.crypto.secret}")
    private String password;


    public String encrypt(String plainText) {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);

        BytesEncryptor encryptor = getEncryptor(salt);

        byte[] cipherBytes = encryptor.encrypt(plainText.getBytes(StandardCharsets.UTF_8));

        byte[] cipherTextWithSalt = ByteBuffer.allocate(salt.length + cipherBytes.length)
                .put(salt)
                .put(cipherBytes)
                .array();

        return new String(Base64.getEncoder().encode(cipherTextWithSalt));
    }

    public String decrypt(String cipherText) {
        byte[] cipherBytesWithSalt = Base64.getDecoder().decode(cipherText);

        ByteBuffer bb = ByteBuffer.wrap(cipherBytesWithSalt);

        byte[] salt = new byte[SALT_LENGTH];
        bb.get(salt);

        byte[] cipherBytes = new byte[bb.remaining()];
        bb.get(cipherBytes);

        BytesEncryptor encryptor = getEncryptor(salt);

        byte[] plainTextBytes = encryptor.decrypt(cipherBytes);
        return new String(plainTextBytes);
    }

    private BytesEncryptor getEncryptor(byte[] salt) {
        String hexSalt = HexUtils.toHexString(salt);
        return Encryptors.stronger(password, hexSalt);
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(password, "CPTN_CRYPTO_SECRET is required to be set in the environment.");
    }
}
