package com.elcptn.mgmtsvc;

import com.elcptn.common.exceptions.BadRequestException;
import com.elcptn.common.exceptions.NotFoundException;
import com.elcptn.common.exceptions.WebApplicationException;
import com.elcptn.common.exceptions.models.AppError;
import com.elcptn.common.validation.OnCreate;
import com.elcptn.mgmtsvc.config.RestExceptionHandler;
import com.elcptn.mgmtsvc.dto.SourceDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/* @author: kc, created on 2/7/23 */
public class ExceptionTests {

    private RestExceptionHandler exceptionHandler = new RestExceptionHandler();
    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void badRequestExceptionTest() {
        BadRequestException exception = new BadRequestException("Invalid data");
        ResponseEntity<AppError> responseEntity = exceptionHandler.handleException(exception, null);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(exception.getMessage(), responseEntity.getBody().getMessage());
        assertEquals(0, responseEntity.getBody().getFieldErrors().size());
    }

    @Test
    void badRequestExceptionWithErrorsTest() {
        FieldError fieldError1 = new FieldError("test", "field1", "field1 is required");
        FieldError fieldError2 = new FieldError("test", "field1", "invalid length");
        FieldError fieldError3 = new FieldError("test", "field2", "field2 is required");

        BadRequestException exception = new BadRequestException("Invalid data", List.of(fieldError1, fieldError2,
                fieldError3));
        ResponseEntity<AppError> responseEntity = exceptionHandler.handleException(exception, null);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(exception.getMessage(), responseEntity.getBody().getMessage());
        assertEquals(2, responseEntity.getBody().getFieldErrors().size());

        List<String> field1Errors = responseEntity.getBody().getFieldErrors().get("field1");
        List<String> field2Errors = responseEntity.getBody().getFieldErrors().get("field2");

        assertEquals(2, field1Errors.size());
        assertEquals(fieldError1.getDefaultMessage(), field1Errors.get(0));
        assertEquals(fieldError2.getDefaultMessage(), field1Errors.get(1));

        assertEquals(1, field2Errors.size());
        assertEquals(fieldError3.getDefaultMessage(), field2Errors.get(0));
    }

    @Test
    void NotFoundExceptionTest() {
        NotFoundException exception = new NotFoundException("Record not found");
        ResponseEntity<AppError> responseEntity = exceptionHandler.handleException(exception, null);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals(exception.getMessage(), responseEntity.getBody().getMessage());
        assertEquals(0, responseEntity.getBody().getFieldErrors().size());
    }

    @Test
    void WebApplicationExceptionTest() {
        WebApplicationException exception = new WebApplicationException("Unknown error");
        ResponseEntity<AppError> responseEntity = exceptionHandler.handleException(exception, null);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals(exception.getMessage(), responseEntity.getBody().getMessage());
        assertEquals(0, responseEntity.getBody().getFieldErrors().size());
    }

    @Test
    void RuntimeExceptionTest() {
        RuntimeException exception = new RuntimeException("Unknown error");
        ResponseEntity<AppError> responseEntity = exceptionHandler.handleException(exception, null);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("There was an error processing your request", responseEntity.getBody().getMessage());
        assertEquals(0, responseEntity.getBody().getFieldErrors().size());
    }

    @Test
    void constraintViolationTest() {
        SourceDto sourceDto = new SourceDto();

        Set<ConstraintViolation<SourceDto>> violations = validator.validate(sourceDto, OnCreate.class);

        ConstraintViolationException exception = new ConstraintViolationException("error message", violations);

        ResponseEntity<AppError> responseEntity = exceptionHandler.handleException(exception, null);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Invalid data", responseEntity.getBody().getMessage());
        assertEquals(1, responseEntity.getBody().getFieldErrors().size());

        List<String> fieldErrors = responseEntity.getBody().getFieldErrors().get("name");

        assertNotNull(fieldErrors);
        assertEquals("Name is required", fieldErrors.get(0));
    }
}
