package com.elcptn.mgmtsvc.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * A DTO for the {@link com.elcptn.mgmtsvc.entities.Event} entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"createdBy", "updatedBy"})
public class EventDto extends BaseDto {

    private UUID workflowId;
}