package com.elcptn.mgmtsvc.exceptions;

public class NotFoundException extends WebApplicationException {

    public NotFoundException(String message) {
        super(message);
    }
}
