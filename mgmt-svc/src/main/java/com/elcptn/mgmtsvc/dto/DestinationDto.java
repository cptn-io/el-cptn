package com.elcptn.mgmtsvc.dto;

import com.elcptn.mgmtsvc.entities.Destination;
import com.elcptn.mgmtsvc.validation.OnCreate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A DTO for the {@link Destination} entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DestinationDto extends BaseDto {

    @NotNull(message = "Name is required", groups = OnCreate.class)
    @Size(min = 3, max = 128, message = "Length must be between 3 and 128 characters")
    private String name;

    @NotBlank(groups = OnCreate.class)
    private String script;

    private Boolean active;
}