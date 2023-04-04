package com.elcptn.mgmtsvc.config;

import com.elcptn.common.exceptions.CommonExceptionHandler;
import com.elcptn.common.exceptions.models.AppError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/* @author: kc, created on 2/7/23 */

@Slf4j
@ControllerAdvice(basePackages = "com.elcptn.mgmtsvc.controllers")
public class RestExceptionHandler extends CommonExceptionHandler {
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<AppError> handleException(Exception ex, WebRequest request) {
        log.debug(ex.getMessage(), ex);
        return super.handleException(ex, request);
    }
}
