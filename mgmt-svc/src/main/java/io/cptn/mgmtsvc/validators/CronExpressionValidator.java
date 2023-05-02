package io.cptn.mgmtsvc.validators;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.field.CronFieldName;
import com.cronutils.model.field.expression.Always;
import com.cronutils.model.field.expression.Every;
import com.cronutils.model.field.expression.FieldExpression;
import com.cronutils.parser.CronParser;
import io.cptn.common.exceptions.BadRequestException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

/* @author: kc, created on 4/4/23 */
@Slf4j
public class CronExpressionValidator implements ConstraintValidator<CronExpression, String> {

    @Override
    public boolean isValid(String cronExpression, ConstraintValidatorContext constraintValidatorContext) {
        if (cronExpression == null) {
            //ignore check for null. @NotNull annotation will take care of it.
            return true;
        }

        try {
            CronParser parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX));
            Cron cron = parser.parse(cronExpression);
            cron.validate();
            FieldExpression minutePart = cron.retrieve(CronFieldName.MINUTE).getExpression();

            if (minutePart instanceof Always || (minutePart instanceof Every && ((Every) minutePart).getPeriod().getValue() < 5)) {
                throw new BadRequestException("Invalid cron expression. Minimum interval must be 5 minutes");
            }
        } catch (Exception e) {
            log.debug("Unable to validate cron expression = " + cronExpression, e);
            return false;
        }

        return true;
    }
}
