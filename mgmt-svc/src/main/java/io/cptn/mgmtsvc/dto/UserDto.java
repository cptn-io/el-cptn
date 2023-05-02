package io.cptn.mgmtsvc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.cptn.common.dto.BaseDto;
import io.cptn.common.validation.OnCreate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/* @author: kc, created on 4/10/23 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class UserDto extends BaseDto {

    @NotNull(message = "First name is required", groups = OnCreate.class)
    @Size(min = 1, max = 100, message = "Length must be less than 100 characters")
    private String firstName;

    @NotNull(message = "Last name is required", groups = OnCreate.class)
    @Size(min = 1, max = 100, message = "Length must be less than 100 characters")
    private String lastName;

    @NotNull(message = "Email is required", groups = OnCreate.class)
    @Size(min = 1, max = 100, message = "Length must be less than 100 characters")
    @Email(message = "Email is invalid")
    private String email;

    @NotNull(message = "Password is required", groups = OnCreate.class)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 5, max = 50, message = "Length must be between 5 and 50 characters")
    private String password;

    private Boolean disabled;

    private Boolean lockedOut;

    private ZonedDateTime lastLoginAt;

    private Boolean mfaEnabled;
}
