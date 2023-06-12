package io.cptn.common.entities;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import io.cptn.common.exceptions.WebApplicationException;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.*;

import java.io.Serial;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

/* @author: kc, created on 4/4/23 */
@MappedSuperclass
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public abstract class BaseSchedule extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;


    @Getter
    @Setter
    private Boolean active = true;

    @Getter
    @Setter
    @Column(name = "cron_expression")
    private String cronExpression;

    @Getter
    @Setter
    @Column(name = "time_zone")
    private String timeZone;

    @Getter
    @Setter
    @Column(columnDefinition = "timestamp with time zone")
    private ZonedDateTime lastRunAt;

    @Getter
    @Setter
    @Column(columnDefinition = "timestamp with time zone")
    private ZonedDateTime nextRunAt;

    public void computeNextRunAt() {
        CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX);
        CronParser parser = new CronParser(cronDefinition);
        Cron cron = parser.parse(this.getCronExpression());

        ZonedDateTime timeNow = ZonedDateTime.now(ZoneId.of(this.getTimeZone()));
        ExecutionTime executionTime = ExecutionTime.forCron(cron);
        Optional<ZonedDateTime> nextExecution = executionTime.nextExecution(timeNow);
        this.setLastRunAt(this.getNextRunAt());
        if (nextExecution.isEmpty()) {
            throw new WebApplicationException("Unable to compute next run time. Ensure that the cron expression is " +
                    "valid");
        }
        this.setNextRunAt(nextExecution.get());
    }
}
