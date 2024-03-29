package io.cptn.mgmtsvc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.cptn.common.dto.BaseDto;
import io.cptn.common.validation.OnCreate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/* @author: kc, created on 3/17/23 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public abstract class ScriptedStepDto extends BaseDto {

    @NotNull(message = "Name is required", groups = OnCreate.class)
    @Size(min = 3, max = 64, message = "Length must be between 3 and 64 characters")
    private String name;

    @NotBlank(groups = OnCreate.class)
    private String script;

    private Boolean active;

    @Valid
    @NotNull(groups = OnCreate.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ConfigItemDto> config;
}
