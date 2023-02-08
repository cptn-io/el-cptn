package com.elcptn.mgmtsvc.helpers;

import org.apache.commons.lang3.RandomStringUtils;

import java.security.SecureRandom;

public class StringHelper {
    public static String getSecureRandomString(int length) {
        return RandomStringUtils.random(length, 0, 0, true, true, null, new SecureRandom());
    }
}
