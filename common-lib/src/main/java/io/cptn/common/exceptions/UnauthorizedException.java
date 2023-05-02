package io.cptn.common.exceptions;

/* @author: kc, created on 2/7/23 */

public class UnauthorizedException extends WebApplicationException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
