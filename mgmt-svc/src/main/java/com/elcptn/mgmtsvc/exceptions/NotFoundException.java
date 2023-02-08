package com.elcptn.mgmtsvc.exceptions;

/* @author: kc, created on 2/7/23 */

public class NotFoundException extends WebApplicationException {

    public NotFoundException(String message) {
        super(message);
    }
}
