package io.cptn.mgmtsvc.dto;

import io.cptn.common.entities.Destination;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * A DTO for the {@link Destination} entity
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class ExtractorDto extends ScriptedStepDto {

    @Serial
    private static final long serialVersionUID = -1843987233036946558L;
}