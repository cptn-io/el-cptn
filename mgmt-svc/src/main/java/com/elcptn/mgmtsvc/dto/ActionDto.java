package com.elcptn.mgmtsvc.dto;

import com.elcptn.mgmtsvc.entities.Action;
import com.elcptn.mgmtsvc.validation.OnCreate;
import com.elcptn.mgmtsvc.validation.OnReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

/**
 * A DTO for the {@link Action} entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActionDto extends BaseDto {

    @NotNull(message = "Name is required", groups = OnCreate.class)
    @Size(min = 5, max = 128, message = "Length must be between 5 and 128 characters")
    private String name;

    @NotBlank(groups = OnCreate.class)
    private String script;

    private Boolean active;

    @NotNull(message = "WorkflowIds are required", groups = OnReference.class)
    private Set<UUID> workflowIds;
}