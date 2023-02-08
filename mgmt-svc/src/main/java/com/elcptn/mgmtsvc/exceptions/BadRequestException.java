package com.elcptn.mgmtsvc.exceptions;

import lombok.Getter;
import org.springframework.validation.FieldError;

import java.util.List;

/* @author: kc, created on 2/7/23 */

public class BadRequestException extends WebApplicationException {
    @Getter
    private List<FieldError> fieldErrors;

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, List<FieldError> fieldErrors) {
        super(message);
        this.fieldErrors = fieldErrors;
    }


}
