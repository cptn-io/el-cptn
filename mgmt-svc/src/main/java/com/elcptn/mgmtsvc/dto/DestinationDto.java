package com.elcptn.mgmtsvc.dto;

import com.elcptn.common.entities.Destination;
import com.elcptn.common.validation.OnCreate;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.util.List;

/**
 * A DTO for the {@link Destination} entity
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class DestinationDto extends ScriptedStepDto {

    @Serial
    private static final long serialVersionUID = 7679901389863628194L;

    @Valid
    @NotNull(groups = OnCreate.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ConfigItemDto> config;
}