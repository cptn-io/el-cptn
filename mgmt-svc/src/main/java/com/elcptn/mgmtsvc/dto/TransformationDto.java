package com.elcptn.mgmtsvc.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * A DTO for the {@link com.elcptn.common.entities.Transformation} entity
 */
@Data
@NoArgsConstructor
public class TransformationDto extends ScriptedStepDto {

    @Serial
    private static final long serialVersionUID = -2058478067827582359L;
}