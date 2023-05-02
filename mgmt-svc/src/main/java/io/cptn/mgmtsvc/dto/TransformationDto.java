package io.cptn.mgmtsvc.dto;

import io.cptn.common.entities.Transformation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * A DTO for the {@link Transformation} entity
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class TransformationDto extends ScriptedStepDto {

    @Serial
    private static final long serialVersionUID = -2058478067827582359L;
}