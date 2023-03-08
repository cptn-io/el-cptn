package com.elcptn.mgmtsvc.dto;

import com.elcptn.mgmtsvc.validation.OnCreate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/* @author: kc, created on 3/7/23 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PipelineDto extends BaseDto {


    @NotNull(message = "Name is required", groups = OnCreate.class)
    @Size(min = 5, max = 128, message = "Length must be between 5 and 128 characters")
    private String name;

    private Boolean active;

    @NotNull(message = "Source is required", groups = OnCreate.class)
    private SourceDto source;

    @NotNull(message = "Destination is required", groups = OnCreate.class)
    private DestinationDto destination;
}
