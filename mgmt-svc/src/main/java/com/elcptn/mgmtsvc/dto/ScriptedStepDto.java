package com.elcptn.mgmtsvc.dto;

import com.elcptn.common.validation.OnCreate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/* @author: kc, created on 3/17/23 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class ScriptedStepDto extends BaseDto {

    @NotNull(message = "Name is required", groups = OnCreate.class)
    @Size(min = 3, max = 64, message = "Length must be between 3 and 64 characters")
    private String name;

    @NotBlank(groups = OnCreate.class)
    private String script;

    private Boolean active;
}
