package io.cptn.ingestionsvc.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.cptn.common.dto.BaseDto;
import io.cptn.common.entities.InboundEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.util.UUID;

/**
 * A DTO for the {@link InboundEvent} entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"createdBy", "updatedBy"})
public class InboundWriteEventDto extends BaseDto {

    @Serial
    private static final long serialVersionUID = -2111486146330125455L;

    private UUID sourceId;
}