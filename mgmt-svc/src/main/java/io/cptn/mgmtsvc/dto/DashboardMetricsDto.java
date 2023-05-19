package io.cptn.mgmtsvc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import io.cptn.common.helpers.JsonHelper;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/* @author: kc, created on 3/9/23 */
@Data
public class DashboardMetricsDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 7840966330976853191L;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<StatusMetricDto> inbound;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<StatusMetricDto> outbound;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String entities;

    public JsonNode getEntities() {
        return JsonHelper.deserializeJson(this.entities);
    }

    public void setEntities(JsonNode entities) {
        this.entities = JsonHelper.serializeJson(entities);
    }
}
