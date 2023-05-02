package io.cptn.mgmtsvc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.cptn.common.dto.BaseDto;
import io.cptn.common.validation.OnCreate;
import io.cptn.mgmtsvc.validators.CronExpression;
import io.cptn.mgmtsvc.validators.TimeZone;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.ZonedDateTime;

/* @author: kc, created on 4/4/23 */
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class PipelineScheduleDto extends BaseDto {

    @NotNull(message = "Cron Expression is required", groups = OnCreate.class)
    @CronExpression
    private String cronExpression;

    @NotNull(message = "TimeZone is required", groups = OnCreate.class)
    @TimeZone
    private String timeZone;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(message = "Pipeline is required", groups = OnCreate.class)
    private PipelineDto pipeline;

    private Boolean active;

    private ZonedDateTime lastRunAt;

    private ZonedDateTime nextRunAt;

}
