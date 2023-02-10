package com.elcptn.mgmtsvc.dto;

import com.elcptn.mgmtsvc.validation.OnCreate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

/**
 * A DTO for the {@link com.elcptn.mgmtsvc.entities.App} entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppDto extends BaseDto {

    @Null
    private String appId;

    @NotNull(message = "Name is required", groups = OnCreate.class)
    @Size(min = 5, max = 128, message = "Length must be between 5 and 128 characters")
    private String name;
}