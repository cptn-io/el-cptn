package io.cptn.mgmtsvc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import io.cptn.common.dto.BaseDto;
import io.cptn.common.helpers.JsonHelper;
import io.cptn.common.validation.OnCreate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serial;
import java.util.List;

/* @author: kc, created on 3/7/23 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
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

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String transformationMap;

    private Boolean batchProcess;

    public JsonNode getTransformationMap() {
        return JsonHelper.deserializeJson(this.transformationMap);
    }

    public void setTransformationMap(JsonNode transformationMap) {
        this.transformationMap = JsonHelper.serializeJson(transformationMap);
    }
}
