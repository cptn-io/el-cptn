package com.elcptn.mgmtsvc.exceptions;

import com.elcptn.mgmtsvc.exceptions.models.AppError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.*;

/* @author: kc, created on 2/7/23 */

@Slf4j
@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<AppError> handleException(Exception ex, WebRequest request) {
        if (log.isDebugEnabled()) {
            log.debug(ex.getMessage(), ex);
        }
        AppError error = new AppError("There was an error processing your request");
        if (ex instanceof HttpMessageNotReadableException) {
            error.setMessage("Unable to process the payload sent");
            return ResponseEntity.unprocessableEntity().body(error);
        } else if (ex instanceof ConstraintViolationException) {
            error.setMessage("Invalid data");
            error.setFieldErrors(processConstraintViolations((ConstraintViolationException) ex));
            return ResponseEntity.badRequest().body(error);
        } else if (ex instanceof WebApplicationException) {
            error.setMessage(ex.getMessage());
            if (ex instanceof BadRequestException) {
                error.setFieldErrors(processFieldErrors((BadRequestException) ex));
                return ResponseEntity.badRequest().body(error);
            } else if (ex instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            return ResponseEntity.internalServerError().body(error);
        }

        log.warn(ex.getMessage(), ex);
        return ResponseEntity.internalServerError().body(error);
    }

    private Map<String, List<String>> processConstraintViolations(ConstraintViolationException ex) {
        Map<String, List<String>> errorDetails = new HashMap<>();

        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        if (violations != null) {
            violations.stream().forEach(e -> {
                String fieldName = null;
                Iterator propertyPathIterator = e.getPropertyPath().iterator();
                while (propertyPathIterator.hasNext()) {
                    fieldName = ((Path.Node) propertyPathIterator.next()).getName();
                }
                List<String> errorList = errorDetails.getOrDefault(fieldName, new ArrayList<>());
                errorList.add(e.getMessage());
                errorDetails.put(fieldName, errorList);
            });
        }
        return errorDetails;
    }

    private Map<String, List<String>> processFieldErrors(BadRequestException ex) {
        Map<String, List<String>> errorDetails = new HashMap<>();

        List<FieldError> fieldErrors = ex.getFieldErrors();
        if (fieldErrors != null) {
            fieldErrors.stream().forEach(e -> {
                String fieldName = e.getField();
                List<String> errorList = errorDetails.getOrDefault(fieldName, new ArrayList<>());
                errorList.add(e.getDefaultMessage());
                errorDetails.put(fieldName, errorList);
            });
        }
        return errorDetails;
    }
}
