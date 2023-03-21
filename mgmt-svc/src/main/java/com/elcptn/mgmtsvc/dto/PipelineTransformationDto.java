package com.elcptn.mgmtsvc.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/* @author: kc, created on 3/20/23 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PipelineTransformationDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 3667268720723396008L;
    @NotNull(message = "Transformation ID is required")
    private UUID transformationId;
}
