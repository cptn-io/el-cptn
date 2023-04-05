package com.elcptn.mgmtsvc.dto;

import com.elcptn.common.dto.BaseDto;
import com.elcptn.common.validation.OnCreate;
import com.elcptn.mgmtsvc.validators.CronExpression;
import com.elcptn.mgmtsvc.validators.TimeZone;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.ZonedDateTime;

/* @author: kc, created on 4/4/23 */
@Data
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
