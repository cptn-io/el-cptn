package com.elcptn.mgmtsvc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

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

    private JsonNode entities;
}
