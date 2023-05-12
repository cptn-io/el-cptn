package io.cptn.common.exceptions;

import org.springframework.security.core.AuthenticationException;

/* @author: kc, created on 5/1/23 */
public class PasswordAuthDisabledException extends AuthenticationException {

    public PasswordAuthDisabledException(String msg) {
        super(msg);
    }
}
