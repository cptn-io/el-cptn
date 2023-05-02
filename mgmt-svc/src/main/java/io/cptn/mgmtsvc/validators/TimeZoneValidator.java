package io.cptn.mgmtsvc.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

/* @author: kc, created on 4/4/23 */
public class TimeZoneValidator implements ConstraintValidator<TimeZone, String> {

    @Override
    public boolean isValid(String timezone, ConstraintValidatorContext constraintValidatorContext) {
        if (timezone == null) {
            // null values are handled by @NotNull
            return true;
        }

        return Set.of(java.util.TimeZone.getAvailableIDs()).contains(timezone);
    }

}
