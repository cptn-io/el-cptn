package com.elcptn.mgmtsvc.dto;

import com.elcptn.common.entities.Destination;
import com.elcptn.mgmtsvc.validation.OnCreate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.util.List;

/**
 * A DTO for the {@link Destination} entity
 */
@Data
@NoArgsConstructor
public class DestinationDto extends ScriptedStepDto {

    @Serial
    private static final long serialVersionUID = 7679901389863628194L;

    @Valid
    @NotNull(groups = OnCreate.class)
    private List<ConfigItemDto> config;
}