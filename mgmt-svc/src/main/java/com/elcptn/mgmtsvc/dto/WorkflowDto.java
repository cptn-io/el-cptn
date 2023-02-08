package com.elcptn.mgmtsvc.dto;

import com.elcptn.mgmtsvc.validation.OnCreate;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

/* @author: kc, created on 2/7/23 */

@Data
public class WorkflowDto extends BaseDto {

    @NotNull(message = "Name is required", groups = OnCreate.class)
    @Size(min = 5, max = 128, message = "Length must be between 5 and 128 characters")
    private String name;
    
    private Boolean secured;

    private Boolean active;

    @Null
    private String primaryKey;

    @Null
    private String secondaryKey;

    @Null
    private ZonedDateTime lastKeyRotationAt;
}
