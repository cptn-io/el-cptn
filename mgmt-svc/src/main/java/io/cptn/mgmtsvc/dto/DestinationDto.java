package io.cptn.mgmtsvc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.cptn.common.entities.Destination;
import io.cptn.common.validation.OnCreate;
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