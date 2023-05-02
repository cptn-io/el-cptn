package io.cptn.mgmtsvc.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/* @author: kc, created on 4/4/23 */
@Constraint(validatedBy = TimeZoneValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TimeZone {
    String message() default "Not a valid TimeZone value";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
