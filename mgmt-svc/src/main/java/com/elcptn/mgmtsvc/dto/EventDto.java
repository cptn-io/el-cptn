package com.elcptn.mgmtsvc.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.util.UUID;

/**
 * A DTO for the {@link com.elcptn.mgmtsvc.entities.Event} entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"createdBy", "updatedBy"})
public class EventDto extends BaseDto {

    @Serial
    private static final long serialVersionUID = -5690341101789142001L;
    private UUID sourceId;
}