package com.elcptn.common.helpers;

import org.apache.commons.lang3.RandomStringUtils;

import java.security.SecureRandom;

/* @author: kc, created on 2/7/23 */

public class StringHelper {
    public static String getSecureRandomString(int length) {
        return RandomStringUtils.random(length, 0, 0, true, true, null, new SecureRandom());
    }
}
