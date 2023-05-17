package io.cptn.mgmtsvc.dto;

import io.cptn.common.dto.BaseDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

/* @author: kc, created on 5/9/23 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class SSOProfileDto extends BaseDto {

    @Serial
    private static final long serialVersionUID = 3640009933885958355L;

    @NotNull(message = "Client Id is required")
    @Size(min = 1, max = 128, message = "Length must be between 1 and 128 characters")
    private String clientId;

    @NotNull(message = "Client Secret is required")
    @Size(min = 1, max = 128, message = "Length must be between 1 and 128 characters")
    private String clientSecret;

    @NotNull(message = "WellKnown OIDC Config URL is required")
    @Pattern(regexp = "^https?:\\/\\/[^\s]+\\/\\.well-known\\/openid-configuration$", message = "URL must end with /" +
            ".well-known/openid-configuration")
    private String wellKnownUrl;

    private Boolean active;

    private Boolean ssoOnly;

    private Boolean enableCreateUser;
}
