package io.cptn.common.exceptions;

import org.springframework.security.core.AuthenticationException;

/* @author: kc, created on 5/1/23 */
public class DemoUserException extends AuthenticationException {

    public DemoUserException(String msg) {
        super(msg);
    }
}
