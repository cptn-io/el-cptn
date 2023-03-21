package com.elcptn.mgmtsvc.dto;

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
    private List<StatusMetricDto> inbound;

    private List<StatusMetricDto> outbound;

    private JsonNode entities;
}
