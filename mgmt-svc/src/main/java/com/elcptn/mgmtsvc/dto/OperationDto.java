package com.elcptn.mgmtsvc.dto;

import com.elcptn.mgmtsvc.entities.Operation;
import com.elcptn.mgmtsvc.entities.OperationType;
import com.elcptn.mgmtsvc.validation.OnCreate;
import com.elcptn.mgmtsvc.validation.OnUpdate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.util.UUID;

/**
 * A DTO for the {@link Operation} entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperationDto extends BaseDto {

    @NotNull(message = "Name is required", groups = OnCreate.class)
    @Size(min = 5, max = 128, message = "Length must be between 5 and 128 characters")
    private String name;

    @NotNull
    private String script;

    @NotNull
    private OperationType type;

    private Integer opVersion;

    @Null
    private String scriptHash;

    private Boolean locked = false;

    @NotNull(message = "App is required", groups = OnCreate.class)
    @Null(message = "App cannot be updated", groups = OnUpdate.class)
    private UUID appId;
}