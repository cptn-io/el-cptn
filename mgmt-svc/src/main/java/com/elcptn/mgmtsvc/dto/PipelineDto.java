package com.elcptn.mgmtsvc.dto;

import com.elcptn.mgmtsvc.validation.OnCreate;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.util.List;

/* @author: kc, created on 3/7/23 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PipelineDto extends BaseDto {


    @Serial
    private static final long serialVersionUID = -5921814550017641462L;
    @NotNull(message = "Name is required", groups = OnCreate.class)
    @Size(min = 3, max = 128, message = "Length must be between 3 and 128 characters")
    private String name;

    private Boolean active;

    @NotNull(message = "Source is required", groups = OnCreate.class)
    private SourceDto source;

    @NotNull(message = "Destination is required", groups = OnCreate.class)
    private DestinationDto destination;


    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<TransformationDto> transformations;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private JsonNode transformationMap;

    private Boolean batchProcess;
}
