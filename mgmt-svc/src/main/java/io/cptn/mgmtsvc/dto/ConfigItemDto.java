package io.cptn.mgmtsvc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@Data
public class ConfigItemDto {

    @NotNull
    @NotBlank(message = "Key is required for configuration")
    private String key;

    @NotNull(message = "Value must not be null")
    private String value;

    private boolean secret;
}
