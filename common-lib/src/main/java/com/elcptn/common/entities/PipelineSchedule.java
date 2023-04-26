package com.elcptn.common.entities;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serial;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

/* @author: kc, created on 4/4/23 */
@Entity
@Table(name = "pipeline_schedule")
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class PipelineSchedule extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 142919398453772084L;


    @Getter
    @Setter
    private Boolean active = true;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipeline_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Pipeline pipeline;

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
        Optional<ZonedDateTime> nextRunAt = executionTime.nextExecution(timeNow);
        this.setLastRunAt(this.getNextRunAt());
        this.setNextRunAt(nextRunAt.get());
    }
}
