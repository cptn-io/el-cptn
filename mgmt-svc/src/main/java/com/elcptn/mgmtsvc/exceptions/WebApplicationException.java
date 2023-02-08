package com.elcptn.mgmtsvc.exceptions;

public class WebApplicationException extends RuntimeException {
    public WebApplicationException(String message) {
        super(message);
    }
}
