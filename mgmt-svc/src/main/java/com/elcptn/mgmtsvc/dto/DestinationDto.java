package com.elcptn.mgmtsvc.dto;

import com.elcptn.mgmtsvc.entities.Destination;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * A DTO for the {@link Destination} entity
 */
@Data
@NoArgsConstructor
public class DestinationDto extends ScriptedStepDto {

    @Serial
    private static final long serialVersionUID = 7679901389863628194L;
}