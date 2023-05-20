package io.cptn.mgmtsvc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
@Data
public class HeaderDto implements Serializable {

    private static final long serialVersionUID = 8007330405364126070L;

    @NotNull
    @NotBlank(message = "Key is required for header")
    private String key;

    @NotNull(message = "Value must not be null")
    private String value;

}
