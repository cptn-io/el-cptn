package com.elcptn.mgmtsvc.dto;

import com.elcptn.common.dto.BaseDto;
import com.elcptn.common.validation.OnCreate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.time.ZonedDateTime;

/* @author: kc, created on 2/7/23 */

/**
 * A DTO for the {@link com.elcptn.common.entities.Source} entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SourceDto extends BaseDto {

    @Serial
    private static final long serialVersionUID = -508186201767170237L;
    @NotNull(message = "Name is required", groups = OnCreate.class)
    @Size(min = 3, max = 64, message = "Length must be between 3 and 64 characters")
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
