package io.cptn.mgmtsvc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
@Data
public class ConfigItemDto implements Serializable {

    private static final long serialVersionUID = 1753130060111985585L;

    @NotNull
    @NotBlank(message = "Key is required for configuration")
    private String key;

    @NotNull(message = "Value must not be null")
    private String value;

    private boolean secret;
}
