package io.cptn.mgmtsvc;

import io.cptn.common.exceptions.models.AppError;
import io.cptn.common.validation.OnCreate;
import io.cptn.mgmtsvc.config.RestExceptionHandler;
import io.cptn.mgmtsvc.dto.SourceDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/* @author: kc, created on 2/7/23 */
class ExceptionTests {

    private RestExceptionHandler exceptionHandler = new RestExceptionHandler();
    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void constraintViolationTest() {
        SourceDto sourceDto = new SourceDto();

        Set<ConstraintViolation<SourceDto>> violations = validator.validate(sourceDto, OnCreate.class);

        ConstraintViolationException exception = new ConstraintViolationException("error message", violations);
        ResponseEntity<AppError> responseEntity = exceptionHandler.handleException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Invalid data", responseEntity.getBody().getMessage());
        assertEquals(1, responseEntity.getBody().getFieldErrors().size());

        List<String> fieldErrors = responseEntity.getBody().getFieldErrors().get("name");

        assertNotNull(fieldErrors);
        assertEquals("Name is required", fieldErrors.get(0));
    }
}
