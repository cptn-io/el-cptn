package io.cptn.mgmtsvc.dto;

import io.cptn.common.dto.BaseDto;
import io.cptn.common.validation.OnCreate;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "Client Id is required", groups = OnCreate.class)
    private String clientId;

    @NotNull(message = "Client Secret is required", groups = OnCreate.class)
    private String clientSecret;

    @NotNull(message = "WellKnown URL is required", groups = OnCreate.class)
    private String wellKnownUrl;

    private Boolean active;

    private Boolean ssoOnly;

    private Boolean enableCreateUser;
}
