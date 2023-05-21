package io.cptn.common.exceptions;

import io.cptn.common.exceptions.models.AppError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;

import java.util.*;

/* @author: kc, created on 2/7/23 */

@Slf4j
public class CommonExceptionHandler {

    public ResponseEntity<AppError> handleException(Exception ex) {
        AppError error = new AppError("There was an error processing your request");
        if (ex instanceof HttpMessageNotReadableException) {
            error.setMessage("Unable to process the payload sent");
            return ResponseEntity.unprocessableEntity().body(error);
        } else if (ex instanceof ConstraintViolationException constraintViolationException) {
            error.setMessage("Invalid data");
            error.setFieldErrors(processConstraintViolations(constraintViolationException));
            return ResponseEntity.badRequest().body(error);
        } else if (ex instanceof WebApplicationException) {
            error.setMessage(ex.getMessage());
            if (ex instanceof BadRequestException badRequestException) {
                error.setFieldErrors(processFieldErrors(badRequestException));
                return ResponseEntity.badRequest().body(error);
            } else if (ex instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            } else if (ex instanceof UnauthorizedException) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
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
                Iterator<Path.Node> propertyPathIterator = e.getPropertyPath().iterator();
                while (propertyPathIterator.hasNext()) {
                    fieldName = propertyPathIterator.next().getName();
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
