package io.cptn.mgmtsvc.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/* @author: kc, created on 4/25/23 */
@Data
public class InstanceInputDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -183566405534637678L;


    @NotNull(message = "Email is required")
    @Size(min = 1, max = 100, message = "Length must be less than 100 characters")
    @Email(message = "Email is invalid")
    private String primaryEmail;

    @Email(message = "Email is invalid")
    private String secondaryEmail;

    @NotNull(message = "Company name is required")
    @Size(min = 1, max = 100, message = "Length must be less than 100 characters")
    private String companyName;

    @AssertTrue(message = "You must accept the terms of service to register an instance")
    private boolean acceptedTerms;

    private boolean collectStats;

}
