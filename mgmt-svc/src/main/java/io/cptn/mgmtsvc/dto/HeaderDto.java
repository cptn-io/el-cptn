package io.cptn.mgmtsvc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@Data
public class HeaderDto {

    @NotNull
    @NotBlank(message = "Key is required for header")
    private String key;

    @NotNull(message = "Value must not be null")
    private String value;
    
}
